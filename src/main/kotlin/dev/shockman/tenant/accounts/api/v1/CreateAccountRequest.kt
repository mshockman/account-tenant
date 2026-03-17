package dev.shockman.tenant.accounts.api.v1

import java.util.UUID

data class CreateAccountRequest(
    val tenantId: UUID,
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val enabled: Boolean = true,
    val attributes: Map<String, Any> = emptyMap()
)