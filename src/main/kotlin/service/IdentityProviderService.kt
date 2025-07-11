package dev.shockman.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import dev.shockman.dto.idp.CreateRealmIdentityProvider
import dev.shockman.dto.idp.UpdateChildIdentityProvider
import dev.shockman.dto.idp.UpdateOidcIdentityProvider
import dev.shockman.dto.idp.toIdentityProviderType
import dev.shockman.entity.IdentityProvider
import dev.shockman.entity.Realm
import dev.shockman.repository.IdentityProviderRepository
import dev.shockman.shared.types.IdentityProviderType
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IdentityProviderService(val idpRepository: IdentityProviderRepository, val objectMapper: ObjectMapper) {
    companion object {
        const val IDENTITY_PROVIDER_NOT_FOUND_EXCEPTION_MESSAGE = "Identity Provider Not Found."
    }

    fun createRealmIdentityProvider(realm: Realm, createRequest: CreateRealmIdentityProvider): IdentityProvider {
        return idpRepository.save(
            IdentityProvider(
                realm = realm,
                tenant = null,
                issuer = createRequest.issuer,
                name = createRequest.name,
                type = createRequest.type.toIdentityProviderType(),
                defaultSyncMode = createRequest.defaultSyncMode,
                config = createRequest.config,
                parent = null,
                enabled = createRequest.enabled
            )
        )
    }

    fun getRealmIdentityProviderById(realm: Realm, id: UUID): IdentityProvider {
        return idpRepository.findRealmIdentityProviderByRealmAndId(realm, id) ?: throw EntityNotFoundException(IDENTITY_PROVIDER_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    fun getRealmScopeIdentityProviderById(realm: Realm, id: UUID): IdentityProvider {
        return idpRepository.findByRealmAndIdAndTenantIsNull(realm, id) ?: throw EntityNotFoundException(IDENTITY_PROVIDER_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    fun update(idp: IdentityProvider, update: UpdateOidcIdentityProvider): IdentityProvider {
        idp.enabled = update.enabled
        idp.defaultSyncMode = update.defaultSyncMode
        idp.issuer = update.issuer
        idp.config = update.config
        idp.name = update.name
        return idpRepository.save(idp)
    }

    fun update(idp: IdentityProvider, update: UpdateChildIdentityProvider): IdentityProvider {
        idp.enabled = update.enabled
        return idpRepository.save(idp)
    }

    fun updateIdentityProvider(identityProvider: IdentityProvider, updateNode: ObjectNode): IdentityProvider {
        return when(identityProvider.type) {
            IdentityProviderType.OIDC -> {
                val updateIdp = objectMapper.treeToValue(updateNode, UpdateOidcIdentityProvider::class.java)
                update(identityProvider, updateIdp)
            }
            IdentityProviderType.CHILD -> {
                val updateIdp = objectMapper.treeToValue(updateNode, UpdateChildIdentityProvider::class.java)
                update(identityProvider, updateIdp)
            }
        }
    }

    fun delete(identityProvider: IdentityProvider) {
        idpRepository.delete(identityProvider)
    }
}