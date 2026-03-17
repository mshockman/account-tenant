package dev.shockman.tenant.accounts.api.v1

data class UpdateAccountRequest(
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val enabled: Boolean = true,
    val attributes: Map<String, Any> = emptyMap()
)