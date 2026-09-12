package dev.shockman.tenant.identity.persistance

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface IdentityRepository : JpaRepository<Identity, UUID> {
    fun findBySubject(subject: String): Identity?
}