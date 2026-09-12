package dev.shockman.tenant.identity.persistance

import dev.shockman.tenant.accounts.persistance.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountIdentityRepository : JpaRepository<AccountIdentity, UUID> {
    fun findByAccountAndIdentity(account: Account, identity: Identity): AccountIdentity?
}