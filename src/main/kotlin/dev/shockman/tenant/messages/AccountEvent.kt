package dev.shockman.tenant.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.tenant.service.OutboxLetter
import java.time.Instant
import java.util.UUID

@OutboxLetter(TENANT_ACCOUNT_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountCreatedEvent(
    val realmId: UUID,
    val tenantId: UUID,
    val accountId: UUID,

    val enabled: Boolean,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val username: String? = null,
    val remoteId: String? = null,
    val attributes: Map<String, Any>? = null,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.ACCOUNT_CREATED,
    override val correlationId: String? = null,
) : BasicOutboxEvent


@OutboxLetter(TENANT_ACCOUNT_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountUpdatedEvent(
    val realmId: UUID,
    val tenantId: UUID,
    val accountId: UUID,

    val enabled: Boolean? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val username: String? = null,
    val remoteId: String? = null,
    val attributes: Map<String, Any>? = null,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.ACCOUNT_UPDATED,
    override val correlationId: String? = null,
) : BasicOutboxEvent


@OutboxLetter(TENANT_ACCOUNT_EVENT_TOPIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDeletedEvent(
    val realmId: UUID,
    val tenantId: UUID,
    val accountId: UUID,

    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant,
    override val type: String = TenantServiceEvents.ACCOUNT_DELETED,
    override val correlationId: String? = null,
) : BasicOutboxEvent