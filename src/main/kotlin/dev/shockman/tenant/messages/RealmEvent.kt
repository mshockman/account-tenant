package dev.shockman.tenant.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.tenant.service.OutboxLetter
import java.time.Instant
import java.util.UUID

@OutboxLetter(TENANT_REALM_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RealmCreatedEvent(
    val realmId: UUID,

    val name: String,
    val slug: String,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.REALM_CREATED,
    override val correlationId: String? = null,
) : BasicOutboxEvent

@OutboxLetter(TENANT_REALM_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RealmUpdatedEvent(
    val realmId: UUID,

    val name: String,
    val slug: String,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.REALM_UPDATED,
    override val correlationId: String? = null,
) : BasicOutboxEvent

@OutboxLetter(TENANT_REALM_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RealmDeletedEvent(
    val realmId: UUID,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.REALM_DELETED,
    override val correlationId: String? = null,
) : BasicOutboxEvent