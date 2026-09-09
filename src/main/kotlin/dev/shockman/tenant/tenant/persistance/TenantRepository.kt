package dev.shockman.tenant.tenant.persistance

import dev.shockman.tenant.realm.persistance.Realm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface TenantRepository : JpaRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {
    fun findByRealmAndSlug(realm: Realm, slug: String): Tenant?
    fun findByRealmAndId(realm: Realm, id: UUID): Tenant?

    fun countByRealm(realm: Realm): Long

    @Query("SELECT t FROM Tenant t JOIN FETCH t.realm WHERE t.id = :id")
    fun findByIdWithRealm(@Param("id") id: UUID): Tenant?
}