package dev.shockman.tenant.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.messaging.core.api.annotations.Message
import dev.shockman.messaging.core.api.annotations.MessageAggregateId
import java.util.UUID

@Message(TENANT_ACCOUNT_EVENT_TOPIC, TenantServiceEvents.ACCOUNT_CREATED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountCreatedEvent(
    val realmId: UUID,
    val tenantId: UUID,
    @MessageAggregateId val accountId: UUID,

    val enabled: Boolean,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val username: String? = null,
    val remoteId: String? = null,
    val attributes: Map<String, Any>? = null,
)


@Message(TENANT_ACCOUNT_EVENT_TOPIC, TenantServiceEvents.ACCOUNT_UPDATED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountUpdatedEvent(
    val realmId: UUID,
    val tenantId: UUID,
    @MessageAggregateId val accountId: UUID,

    val enabled: Boolean? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val username: String? = null,
    val remoteId: String? = null,
    val attributes: Map<String, Any>? = null,
)


@Message(TENANT_ACCOUNT_EVENT_TOPIC, TenantServiceEvents.ACCOUNT_DELETED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountDeletedEvent(
    val realmId: UUID,
    val tenantId: UUID,
    @MessageAggregateId val accountId: UUID,
)