package dev.shockman.dto.idp

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdpConfig
import dev.shockman.shared.types.OidcIdpConfig
import java.util.UUID

@JsonDeserialize(using = CreateTenantIdentityProviderDeserializer::class)
interface CreateTenantIdentityProvider {
    val type: TenantIdentityProviderType
}

data class CreateOidcTenantIdentityProvider(
    override val type: TenantIdentityProviderType,
    val name: String,
    val issuer: String,
    val defaultSyncMode: IdentityProviderMapperSyncMode,
    val config: IdpConfig,
    val enabled: Boolean
) : CreateTenantIdentityProvider

data class CreateChildIdentityProvider(
    val parent: UUID,
    val name: String?,
    val enabled: Boolean
) : CreateTenantIdentityProvider {
    override val type: TenantIdentityProviderType = TenantIdentityProviderType.CHILD
}

class CreateTenantIdentityProviderDeserializer : JsonDeserializer<CreateTenantIdentityProvider>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CreateTenantIdentityProvider {
        val root = p.codec.readTree<ObjectNode>(p)
        val type = TenantIdentityProviderType.valueOf(root["type"].asText())

        return when(type) {
            TenantIdentityProviderType.OIDC -> CreateOidcTenantIdentityProvider(
                type = type,
                name = root["name"].asText(),
                issuer = root["issuer"].asText(),
                defaultSyncMode = IdentityProviderMapperSyncMode.valueOf(root["defaultSyncMode"].asText()),
                config = p.codec.treeToValue(root["config"], OidcIdpConfig::class.java),
                enabled = root["enabled"].asBoolean()
            )
            TenantIdentityProviderType.CHILD -> CreateChildIdentityProvider(
                parent = UUID.fromString(root["parent"].asText()),
                name = root["name"].asText(),
                enabled = root["enabled"].asBoolean()
            )
        }
    }
}