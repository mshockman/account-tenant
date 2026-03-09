package dev.shockman.tenant.api.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.messaging.core.api.annotations.Message
import dev.shockman.messaging.core.api.annotations.MessageAggregateId
import java.time.Instant
import java.util.UUID

@Message(TENANT_REALM_EVENT_TOPIC, TenantServiceEvents.REALM_CREATED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RealmCreatedEvent(
    @MessageAggregateId val realmId: UUID,

    val name: String,
    val slug: String,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Message(TENANT_REALM_EVENT_TOPIC, TenantServiceEvents.REALM_UPDATED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RealmUpdatedEvent(
    @MessageAggregateId val realmId: UUID,
    val name: String,
    val slug: String,

    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

@Message(TENANT_REALM_EVENT_TOPIC, TenantServiceEvents.REALM_DELETED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class RealmDeletedEvent(
    @MessageAggregateId val realmId: UUID
)