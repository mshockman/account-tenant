package dev.shockman.service

import com.fasterxml.jackson.databind.JsonNode
import dev.shockman.entity.OutboxMessage
import dev.shockman.shared.logError
import dev.shockman.shared.logInfo
import dev.shockman.shared.logTrace
import dev.shockman.shared.logWarn
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class OutboxLetter(
    val topic: String,
    val messageType: String = ""
)

@Service
class OutboxPollingService(
    private val outboxService: OutboxService,
    private val kafkaTemplate: KafkaTemplate<String, JsonNode>,
    private val clock: Clock
) {
    fun pollAndProcess(processId: String, lease: Duration, renewWindow: Duration, limit: Int = 10) {
        logInfo { "Polling for messages with processId: $processId" }
        val ids = outboxService.claimRows(processId, lease, limit)
        val messages = outboxService.findAllByIds(ids)
        val start = clock.instant()

        logInfo { "Processing ${messages.size} messages" }
        if(messages.isEmpty()) return logInfo { "No messages to process" }
        logTrace { "Messages: ${ids.joinToString(", ")}" }

        val loggingContext = mapOf("processId" to processId, "limit" to limit, "lease" to lease.toString())

        messages.forEach { msg ->
            if(!renewLeaseIfNeeded(msg, processId, lease, renewWindow)) {
                logWarn { "Message lease was lost or expired for message id: ${msg.eventId}" }
                return@forEach
            }

            try {
                logTrace(loggingContext) { "Processing message: ${msg.eventId}" }

                val message = createKafkaMessage(msg)
                val claimedExpiresAt = msg.claimExpiresAt ?: throw IllegalStateException("claimExpiresAt is null during processing.  Shouldn't be possible.")
                val timeLeft = Duration.between(clock.instant(), claimedExpiresAt)

                if(timeLeft.isZero || timeLeft.isNegative) {
                    throw TimeoutException("Timed out processing of message")
                }

                val await = timeLeft.minusMillis(500).coerceAtLeast(Duration.ofMillis(1))

                kafkaTemplate.send(message).get(await.toMillis(), TimeUnit.MILLISECONDS)
                outboxService.markComplete(msg)
            } catch(_: TimeoutException) {
                logWarn(context = loggingContext) { "Timed out processing of message: ${msg.eventId}" }
                outboxService.reschedule(msg, error = "Timeout")
            } catch (ie: InterruptedException) {
                logWarn(ie, loggingContext) { "Interrupted processing of message: ${msg.eventId}" }
                outboxService.reschedule(msg, error = "interrupted: ${ie.message}")
                Thread.currentThread().interrupt()
            } catch (ee: ExecutionException) {
                val cause = ee.cause ?: ee
                logError(cause, loggingContext) { "Kafka send failed: ${msg.eventId}" }
                outboxService.reschedule(msg, error = ee.message)
            } catch (e: Exception) {
                val cause = e.cause ?: e
                logError(cause, loggingContext) { "Error processing message: ${msg.eventId}" }
                outboxService.reschedule(msg, error = cause.message)
            }
        }

        val duration = Duration.between(start, clock.instant())
        logInfo { "Batch complete: claimed=${messages.size} duration=${duration.toMillis()}ms" }
    }

    private fun renewLeaseIfNeeded(message: OutboxMessage, processId: String, lease: Duration, renewWindow: Duration): Boolean {
        val claimExpiresAt = message.claimExpiresAt ?: return false
        val id = message.id ?: return false
        val timeLeft = Duration.between(clock.instant(), claimExpiresAt)
        val clampedRenewWindow = renewWindow.coerceAtMost(lease)

        if(timeLeft.isZero || timeLeft.isNegative) {
            return false
        }

        return if(timeLeft <= clampedRenewWindow) {
            val newExpires = clock.instant() + lease
            val wasRenewed = outboxService.renewLease(id, processId, newExpires)

            if(wasRenewed) {
                message.claimExpiresAt = newExpires
            }

            wasRenewed
        } else {
            true
        }
    }

    private fun createKafkaMessage(message: OutboxMessage): Message<JsonNode> {
        var builder = MessageBuilder
            .withPayload(message.message)
            .setHeader(KafkaHeaders.TOPIC, message.topic)
            .setHeader(KafkaHeaders.KEY, message.aggregateId)
            .setHeader("eventId", message.eventId.toString())
            .setHeader("messageType", message.messageType)

        message.correlationId?.let { builder = builder.setHeader("correlationId", it) }

        return builder.build()
    }
}
