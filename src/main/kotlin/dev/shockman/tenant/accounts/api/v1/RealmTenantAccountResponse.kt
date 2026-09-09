package dev.shockman.tenant.accounts.api.v1

import dev.shockman.tenant.accounts.persistance.Account
import java.time.Instant
import java.util.UUID

data class RealmTenantAccountResponse(
    val id: UUID,
    val tenantId: UUID,
    val tenantName: String,
    val realmId: UUID,
    val realmName: String,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val enabled: Boolean,
    val attributes: Map<String, Any>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val remoteId: String?
)

fun Account.toRealmTenantAccount(): RealmTenantAccountResponse {
    return RealmTenantAccountResponse(
        id = id,
        tenantId = tenant.id,
        tenantName = tenant.name,
        realmId = tenant.realm.id,
        realmName = tenant.realm.name,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        enabled = enabled,
        attributes = attributes,
        createdAt = requireNotNull(createdAt) { "created timestamp is required" },
        updatedAt = requireNotNull(updatedAt) { "updated timestamp is required" },
        remoteId = remoteId,
    )
}