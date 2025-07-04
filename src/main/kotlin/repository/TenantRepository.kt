package dev.shockman.repository

import dev.shockman.entity.Realm
import dev.shockman.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TenantRepository : JpaRepository<Tenant, UUID> {
    fun findByRealmAndSlug(realm: Realm, slug: String): Tenant?
    fun findByRealmAndId(realm: Realm, id: UUID): Tenant?
}