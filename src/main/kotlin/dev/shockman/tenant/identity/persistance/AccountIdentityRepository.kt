package dev.shockman.tenant.identity.persistance

import dev.shockman.tenant.accounts.persistance.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface AccountIdentityRepository : JpaRepository<AccountIdentity, UUID> {
    fun findByAccountAndIdentity(account: Account, identity: Identity): AccountIdentity?

    @Modifying
    @Query(
        value = """
            INSERT INTO account_identity (account_id, identity_id)
            VALUES (:accountId, :identityId)
            ON CONFLICT (account_id, identity_id) DO NOTHING
        """,
        nativeQuery = true
    )
    fun createIfAbsent(
        @Param("accountId") accountId: UUID,
        @Param("identityId") identityId: UUID
    ): Int
}