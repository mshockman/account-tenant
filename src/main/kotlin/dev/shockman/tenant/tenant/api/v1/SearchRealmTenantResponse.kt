package dev.shockman.tenant.tenant.api.v1

import java.util.UUID

data class SearchRealmTenantResponse(
    val tenants: List<TenantResponse>,
    val realmId: UUID,
    val cursor: String? = null,
)