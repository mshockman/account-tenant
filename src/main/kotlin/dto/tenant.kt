package dev.shockman.dto

import dev.shockman.entity.Tenant
import java.time.Instant
import java.util.UUID

data class CreateTenantRequest(
    val slug: String,
    val name: String,
    val enabled: Boolean = true,
)

data class TenantResponse(
    val id: UUID,
    val slug: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val enabled: Boolean,
)

data class UpdateTenant(
    val name: String? = null,
    val enabled: Boolean? = null
)

fun Tenant.toResponse(): TenantResponse {
    return TenantResponse(
        id = id,
        slug = slug,
        name = name,
        createdAt = requireNotNull(createdAt) { "created timestamp is required" },
        updatedAt = requireNotNull(updatedAt) { "updated timestamp is required" },
        enabled = enabled,
    )
}