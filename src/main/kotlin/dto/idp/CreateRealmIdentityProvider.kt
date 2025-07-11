package dev.shockman.dto.idp

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode
import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdpConfig
import dev.shockman.shared.types.OidcIdpConfig

@JsonDeserialize(using = CreateRealmIdentityProviderDeserializer::class)
data class CreateRealmIdentityProvider(
    val name: String,
    val issuer: String,
    val type: RealmIdentityProviderType,
    val defaultSyncMode: IdentityProviderMapperSyncMode,
    val config: IdpConfig,
    val enabled: Boolean
)

class CreateRealmIdentityProviderDeserializer : JsonDeserializer<CreateRealmIdentityProvider>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CreateRealmIdentityProvider {
        val root = p.codec.readTree<ObjectNode>(p)
        val type = RealmIdentityProviderType.valueOf(root["type"].asText())

        val config = when(type) {
            RealmIdentityProviderType.OIDC -> p.codec.treeToValue(root["config"], OidcIdpConfig::class.java)
        }

        return CreateRealmIdentityProvider(
            name = root["name"].asText(),
            issuer = root["issuer"].asText(),
            type = type,
            defaultSyncMode = IdentityProviderMapperSyncMode.valueOf(root["defaultSyncMode"].asText()),
            config = config,
            enabled = root["enabled"].asBoolean()
        )
    }
}