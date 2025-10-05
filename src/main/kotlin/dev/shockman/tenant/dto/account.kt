package dev.shockman.tenant.dto

import dev.shockman.tenant.entity.Account
import java.time.Instant
import java.util.UUID

data class CreateAccountRequest(
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val enabled: Boolean = true,
    val attributes: Map<String, Any> = emptyMap()
)

data class UpdateAccountRequest(
    val username: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val enabled: Boolean = true,
    val attributes: Map<String, Any> = emptyMap()
)

data class RealmTenantAccountResponse(
    val id: UUID,
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