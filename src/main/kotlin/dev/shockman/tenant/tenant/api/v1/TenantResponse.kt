package dev.shockman.tenant.tenant.api.v1

import dev.shockman.tenant.tenant.persistance.Tenant
import java.time.Instant
import java.util.UUID

data class TenantResponse(
    val id: UUID,
    val slug: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val enabled: Boolean,
    val realmId: UUID
)

fun Tenant.toResponse(): TenantResponse {
    return TenantResponse(
        id = id,
        slug = slug,
        name = name,
        createdAt = requireNotNull(createdAt) { "created timestamp is required" },
        updatedAt = requireNotNull(updatedAt) { "updated timestamp is required" },
        enabled = enabled,
        realmId = realm.id
    )
}