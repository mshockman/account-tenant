package dev.shockman.tenant.accounts.api.v1

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Size
import java.util.UUID

data class SearchAccountRequest(
    val tenantId: UUID? = null,

    val realmId: UUID? = null,

    @field:Size(max = 100)
    val query: String? = null,

    @field:Max(100)
    val limit: Int = 10,

    val cursor: String? = null,
)
