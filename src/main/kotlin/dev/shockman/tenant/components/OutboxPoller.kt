package dev.shockman.tenant.components

import dev.shockman.tenant.config.properties.OutboxConfigurationProperties
import dev.shockman.tenant.service.OutboxPollingService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OutboxPoller(
    private val outboxPollingService: OutboxPollingService,
    private val outboxSettings: OutboxConfigurationProperties
) {
    private val id = buildString {
        append(UUID.randomUUID().toString())
        System.getenv("HOSTNAME")?.let { append("@$it") }
    }

    @Scheduled(initialDelayString = $$"${outbox.initial-delay}", fixedDelayString = $$"${outbox.fixed-delay}")
    fun poll() {
        outboxPollingService.pollAndProcess(
            id,
            outboxSettings.messageLease,
            outboxSettings.renewWindow,
            outboxSettings.messagePollLimit
        )
    }
}