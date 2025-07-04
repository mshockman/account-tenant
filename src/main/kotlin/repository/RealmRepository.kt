package dev.shockman.repository

import dev.shockman.entity.Realm
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RealmRepository: JpaRepository<Realm, UUID> {
    fun findBySlug(slug: String): Realm?
}