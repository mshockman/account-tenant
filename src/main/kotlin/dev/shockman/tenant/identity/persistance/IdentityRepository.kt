package dev.shockman.tenant.identity.persistance

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface IdentityRepository : JpaRepository<Identity, UUID> {
    fun findBySubject(subject: String): Identity?

    @Modifying
    @Query(
        value = """
            INSERT INTO identity (realm_id, username, subject, email, email_verified, phone, phone_verified)
            VALUES (
            :realmId, :username, :subject, :email, :emailVerified, :phone, :phoneVerified
            )
            ON CONFLICT (subject) DO UPDATE 
            SET username = :username, email = :email, email_verified = :emailVerified, phone = :phone, phone_verified = :phoneVerified
        """,
        nativeQuery = true
    )
    fun upsert(realmId: UUID, username: String, subject: String, email: String?, emailVerified: Boolean, phone: String?, phoneVerified: Boolean): Int
}