package dev.shockman.tenant.repository

import dev.shockman.tenant.entity.Realm
import dev.shockman.tenant.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TenantRepository : JpaRepository<Tenant, UUID> {
    fun findByRealmAndSlug(realm: Realm, slug: String): Tenant?
    fun findByRealmAndId(realm: Realm, id: UUID): Tenant?
}