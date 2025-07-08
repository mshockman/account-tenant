package dev.shockman.service

import dev.shockman.dto.CreateIdentityProvider
import dev.shockman.dto.UpdateRealmIdentityProvider
import dev.shockman.entity.IdentityProvider
import dev.shockman.entity.Realm
import dev.shockman.repository.IdentityProviderRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IdentityProviderService(val idpRepository: IdentityProviderRepository) {
    companion object {
        const val IDENTITY_PROVIDER_NOT_FOUND_EXCEPTION_MESSAGE = "Identity Provider Not Found."
    }

    fun createRealmIdentityProvider(realm: Realm, createRequest: CreateIdentityProvider): IdentityProvider {
        return idpRepository.save(
            IdentityProvider(
                realm = realm,
                tenant = null,
                issuer = createRequest.issuer,
                name = createRequest.name,
                type = createRequest.type,
                defaultSyncMode = createRequest.defaultSyncMode,
                config = createRequest.config
            )
        )
    }

    fun getRealmIdentityProviderById(realm: Realm, id: UUID): IdentityProvider {
        return idpRepository.findRealmIdentityProviderByRealmAndId(realm, id) ?: throw EntityNotFoundException(IDENTITY_PROVIDER_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    fun getRealmScopeIdentityProviderById(realm: Realm, id: UUID): IdentityProvider {
        return idpRepository.findByRealmAndIdAndTenantIsNull(realm, id) ?: throw EntityNotFoundException(IDENTITY_PROVIDER_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    fun updateIdentityProvider(identityProvider: IdentityProvider, update: UpdateRealmIdentityProvider): IdentityProvider {
        identityProvider.issuer = update.issuer
        identityProvider.name = update.name;
        identityProvider.config = update.config
        identityProvider.defaultSyncMode = update.defaultSyncMode
        return idpRepository.save(identityProvider)
    }

    fun delete(identityProvider: IdentityProvider) {
        idpRepository.delete(identityProvider)
    }
}