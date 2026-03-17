package dev.shockman.tenant.tenant.api.v1

data class UpdateTenant(
    val name: String? = null,
    val enabled: Boolean? = null
)