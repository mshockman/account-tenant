package dev.shockman.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.service.OutboxLetter
import java.time.Instant
import java.util.UUID


@OutboxLetter(TENANT_TENANT_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantCreatedEvent(
    val realmId: UUID,
    val tenantId: UUID,

    val name: String,
    val slug: String,
    val enabled: Boolean,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.TENANT_CREATED,
    override val correlationId: String? = null,
) : BasicOutboxEvent

@OutboxLetter(TENANT_TENANT_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantUpdatedEvent(
    val realmId: UUID,
    val tenantId: UUID,

    val name: String,
    val slug: String,
    val enabled: Boolean,

    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.TENANT_UPDATED,
    override val correlationId: String? = null,
) : BasicOutboxEvent

@OutboxLetter(TENANT_TENANT_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantDeletedEvent(
    val realmId: UUID,
    val tenantId: UUID,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.TENANT_DELETED,
    override val correlationId: String? = null,
) : BasicOutboxEvent