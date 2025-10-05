package dev.shockman.tenant.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.shockman.tenant.config.properties.OutboxConfigurationProperties
import dev.shockman.tenant.entity.OutboxMessage
import dev.shockman.tenant.entity.OutboxMessageStatus
import dev.shockman.tenant.messages.BasicOutboxEvent
import dev.shockman.tenant.repository.OutboxRepository
import dev.shockman.tenant.shared.logError
import dev.shockman.tenant.shared.logTrace
import dev.shockman.tenant.shared.logWarn
import io.micrometer.tracing.Tracer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Duration
import java.time.Instant
import kotlin.reflect.full.findAnnotation

@Service
class OutboxService(
    private val mapper: ObjectMapper,
    private val outboxRepository: OutboxRepository,
    private val outboxSettings: OutboxConfigurationProperties,
    private val clock: Clock,
    private val tracer: Tracer
) {
    @Transactional(propagation = Propagation.MANDATORY)
    fun <T: BasicOutboxEvent> send(aggregateId: String, message: T): OutboxMessage {
        val kClass = message::class
        val letterMetaData = kClass.findAnnotation<OutboxLetter>() ?: error("${kClass.qualifiedName} not annotated with @OutboxLetter")
        val messageType = letterMetaData.messageType.ifBlank { message.type }

        val node = mapper.valueToTree<JsonNode>(message)

        val messageEntity = OutboxMessage(
            eventId = message.eventId,
            aggregateId = aggregateId,
            messageType = messageType,
            message = node,
            topic = letterMetaData.topic,
            correlationId = message.correlationId ?: tracer.currentSpan()?.context()?.traceId(),
        )

        return outboxRepository.save(messageEntity)
    }

    @Transactional
    fun claimRows(processId: String, duration: Duration, limit: Int = 10): List<Long> {
        return outboxRepository.claimRows(processId, duration.seconds, limit)
    }

    fun findAllByIds(ids: List<Long>): List<OutboxMessage> {
        if(ids.isEmpty()) return emptyList()
        val messages = outboxRepository.findAllById(ids).associateBy { it.id }
        return ids.mapNotNull { messages[it] }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun markComplete(message: OutboxMessage) {
        logTrace { "Marking message: ${message.eventId} as processed" }
        message.status = OutboxMessageStatus.SUCCESS
        message.processedAt = clock.instant()
        message.claimExpiresAt = null
        message.claimedAt = null
        message.claimedBy = null
        outboxRepository.save(message)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun reschedule(message: OutboxMessage, error: String? = null, wait: Duration = Duration.ofSeconds(60)) {
        val loggingContext = mapOf(
            "messageId" to message.id.toString(),
            "topic" to message.topic,
            "eventId" to message.eventId.toString(),
            "aggregateId" to message.aggregateId,
            "messageType" to message.messageType,
            "attempt" to message.attempt + 1,
        )

        if(message.attempt + 1 >= outboxSettings.maxAttempts && outboxSettings.maxAttempts != -1) {
            logError(context = loggingContext) { "Maximum number of attempts reached for message: ${message.eventId}" }
            message.status = OutboxMessageStatus.FAILED
            message.error = error
            message.claimExpiresAt = null
            message.attempt += 1
            outboxRepository.save(message)
        } else {
            logWarn(context = loggingContext) { "Rescheduling message: ${message.eventId} for retry in ${wait.seconds} seconds" }
            message.status = OutboxMessageStatus.BACKOFF
            message.availableAt = clock.instant() + wait
            message.error = error
            message.attempt += 1
            message.claimExpiresAt = null
            outboxRepository.save(message)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun renewLease(id: Long, processId: String, duration: Duration): Boolean {
        return outboxRepository.renewLease(id, processId, duration.seconds) == 1
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun renewLease(id: Long, processId: String, newExpires: Instant): Boolean {
        return outboxRepository.renewLease(id, processId, newExpires) == 1
    }

    fun getClaimExpiresAtById(id: Long): Instant? {
        return outboxRepository.getClaimExpiresAtById(id)
    }
}