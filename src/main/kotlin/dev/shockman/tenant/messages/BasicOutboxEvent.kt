package dev.shockman.tenant.messages

import java.time.Instant
import java.util.UUID

sealed interface BasicOutboxEvent {
    val type: String
    val occurredAt: Instant
    val eventId: UUID
    val correlationId: String?
}
