package dev.shockman.repository

import dev.shockman.entity.Tenant
import dev.shockman.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findAccountByTenantAndId(tenant: Tenant, id: UUID): Account?
    fun findAccountByTenantAndUsername(tenant: Tenant, username: String): Account?
    fun findAccountByTenantAndEmail(tenant: Tenant, email: String): Account?
    fun findAccountByTenantAndPhone(tenant: Tenant, phone: String): Account?
}