package dev.shockman.dto.idp

import dev.shockman.entity.IdentityProvider
import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdentityProviderType
import dev.shockman.shared.types.IdpConfig
import java.time.Instant
import java.util.UUID

data class IdentityProviderResponse(
    val id: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val tenantId: UUID?,
    val parentId: UUID?,
    val type: IdentityProviderType?,
    val issuer: String?,
    val name: String?,
    var defaultSyncMode: IdentityProviderMapperSyncMode?,
    val enabled: Boolean,
    val config: IdpConfig?
)

fun IdentityProvider.toIdentityProviderResponse(): IdentityProviderResponse {
    return IdentityProviderResponse(
        id = id,
        createdAt = requireNotNull(createdAt),
        updatedAt = requireNotNull(updatedAt),
        tenantId = tenant?.id,
        parentId = parent?.id,
        type = type,
        issuer = issuer,
        name = name,
        defaultSyncMode = defaultSyncMode,
        enabled = enabled,
        config = config
    )
}