package dev.shockman.tenant.api.messages

import com.fasterxml.jackson.annotation.JsonInclude
import dev.shockman.messaging.core.api.annotations.Message
import java.util.UUID


@Message(TENANT_IDENTITY_EVENT_TOPIC, TenantServiceEvents.IDENTITY_CREATED)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class IdentityCreatedEvent(
    val id: UUID,
    val username: String,
    val subject: String,
    val email: String?,
    val emailVerified: Boolean,
    val phone: String?,
    val phoneVerified: Boolean,
)