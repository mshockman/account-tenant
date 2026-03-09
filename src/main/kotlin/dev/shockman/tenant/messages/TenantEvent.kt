package dev.shockman.tenant.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.messaging.core.api.annotations.Message
import dev.shockman.messaging.core.api.annotations.MessageAggregateId
import java.util.UUID
import java.time.Instant


@Message(TENANT_TENANT_EVENT_TOPIC, TenantServiceEvents.TENANT_DELETED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantCreatedEvent(
    val realmId: UUID,
    @MessageAggregateId val tenantId: UUID,

    val name: String,
    val slug: String,
    val enabled: Boolean,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Message(TENANT_TENANT_EVENT_TOPIC, TenantServiceEvents.TENANT_DELETED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantUpdatedEvent(
    val realmId: UUID,
    @MessageAggregateId val tenantId: UUID,

    val name: String,
    val slug: String,
    val enabled: Boolean,

    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long,
)

@Message(TENANT_TENANT_EVENT_TOPIC, TenantServiceEvents.TENANT_DELETED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TenantDeletedEvent(
    val realmId: UUID,
    @MessageAggregateId val tenantId: UUID,
)