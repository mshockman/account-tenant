package dev.shockman.repository

import dev.shockman.entity.IdentityProvider
import dev.shockman.entity.Realm
import dev.shockman.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IdentityProviderRepository : JpaRepository<IdentityProvider, UUID> {
    fun findTenantIdentityProviderByTenantAndId(tenant: Tenant, id: UUID): IdentityProvider?
    fun findRealmIdentityProviderByRealmAndId(realm: Realm, id: UUID): IdentityProvider?
    fun findByRealmAndIdAndTenantIsNull(realm: Realm, id: UUID): IdentityProvider?
}