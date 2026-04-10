package dev.shockman.tenant.accounts.api.v1

import java.util.UUID

data class SearchAccountResponse(
    val accounts: List<RealmTenantAccountResponse>,
    val tenantId: UUID? = null,
    val cursor: String? = null,
)
