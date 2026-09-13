package dev.shockman.tenant.tenant.api.v1

import java.util.UUID

data class CreateTenantRequest(
    val realmId: UUID,
    val name: String,
    val enabled: Boolean = true,
)