package dev.shockman.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import dev.shockman.entity.IdentityProvider
import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdentityProviderType
import dev.shockman.shared.types.IdpConfig
import dev.shockman.shared.types.OidcIdpConfig
import java.time.Instant
import java.util.UUID

class CreateIdentityProviderDeserializer : JsonDeserializer<CreateIdentityProvider>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): CreateIdentityProvider {
        val root = parser.codec.readTree<ObjectNode>(parser)
        val type = IdentityProviderType.valueOf(root["type"].asText())
        val configNode = root["config"]

        val config = when(type) {
            IdentityProviderType.OIDC -> parser.codec.treeToValue(configNode, OidcIdpConfig::class.java)
        }

        return CreateIdentityProvider(
            issuer = root["issuer"].asText(),
            name = root["name"].asText(),
            type = type,
            defaultSyncMode = IdentityProviderMapperSyncMode.valueOf(root["defaultSyncMode"].asText()),
            config = config
        )
    }

}

@JsonDeserialize(using = CreateIdentityProviderDeserializer::class)
data class CreateIdentityProvider (
    val issuer: String,
    val name: String,
    val type: IdentityProviderType,
    val defaultSyncMode: IdentityProviderMapperSyncMode,
    val config: OidcIdpConfig
)

data class RealmIdentityProviderResponse(
    val id: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val issuer: String,
    val name: String,
    val type: IdentityProviderType,
    val defaultSyncMode: IdentityProviderMapperSyncMode,
    val config: IdpConfig
)

fun IdentityProvider.toRealmIdentityProviderResponse(): RealmIdentityProviderResponse {
    return RealmIdentityProviderResponse(
        id = id,
        createdAt = requireNotNull(createdAt),
        updatedAt = requireNotNull(updatedAt),
        issuer = issuer,
        name = requireNotNull(name),
        type = requireNotNull(type),
        defaultSyncMode = requireNotNull(defaultSyncMode),
        config = config
    )
}

data class TenantIdentityProviderResponse(
    val id: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val issuer: String,
    val name: String?,
    val type: IdentityProviderType?,
    val defaultSyncMode: IdentityProviderMapperSyncMode?,
    val config: IdpConfig
)

fun IdentityProvider.toTenantIdentityProviderResponse(): TenantIdentityProviderResponse {
    return TenantIdentityProviderResponse(
        id = id,
        createdAt = requireNotNull(createdAt),
        updatedAt = requireNotNull(updatedAt),
        issuer = issuer,
        name = name,
        type = type,
        defaultSyncMode = defaultSyncMode,
        config = config
    )
}

data class IdentityProviderResponse(
    val id: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
    val tenantId: UUID?,
    val issuer: String,
    val name: String?,
    val type: IdentityProviderType?,
    val defaultSyncMode: IdentityProviderMapperSyncMode?,
    val config: IdpConfig
)

fun IdentityProvider.toIdentityProviderResponse(): IdentityProviderResponse {
    return IdentityProviderResponse(
        id = id,
        createdAt = requireNotNull(createdAt),
        updatedAt = requireNotNull(updatedAt),
        tenantId = tenant?.id,
        issuer = issuer,
        name = name,
        type = type,
        defaultSyncMode = defaultSyncMode,
        config = config
    )
}

data class UpdateRealmIdentityProvider(
    val issuer: String,
    val name: String,
    val defaultSyncMode: IdentityProviderMapperSyncMode,
    val config: IdpConfig
)