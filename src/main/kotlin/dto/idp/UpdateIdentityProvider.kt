package dev.shockman.dto.idp

import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdpConfig
import dev.shockman.shared.types.OidcIdpConfig

interface UpdateIdentityProvider {
    val enabled: Boolean
}

interface UpdateParentIdentityProvider : UpdateIdentityProvider{
    val name: String
    val issuer: String
    val defaultSyncMode: IdentityProviderMapperSyncMode
    val config: IdpConfig
}

data class UpdateOidcIdentityProvider(
    override val name: String,
    override val issuer: String,
    override val defaultSyncMode: IdentityProviderMapperSyncMode,
    override val config: OidcIdpConfig,
    override val enabled: Boolean
) : UpdateParentIdentityProvider

data class UpdateChildIdentityProvider(
    override val enabled: Boolean
) : UpdateIdentityProvider

