package dev.shockman.tenant.repository

import dev.shockman.tenant.entity.Realm
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RealmRepository: JpaRepository<Realm, UUID> {
    fun findBySlug(slug: String): Realm?

    fun findByNameContainingIgnoreCase(name: String): List<Realm>
}