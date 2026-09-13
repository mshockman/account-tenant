package dev.shockman.tenant.realm.persistance

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RealmRepository: JpaRepository<Realm, UUID> {
    fun findByNameContainingIgnoreCase(name: String): List<Realm>
}