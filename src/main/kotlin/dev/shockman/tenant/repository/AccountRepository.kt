package dev.shockman.tenant.repository

import dev.shockman.tenant.entity.Account
import dev.shockman.tenant.entity.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID> {
    fun findAccountByTenantAndId(tenant: Tenant, id: UUID): Account?
    fun findAccountByTenantAndUsername(tenant: Tenant, username: String): Account?
    fun findAccountByTenantAndEmail(tenant: Tenant, email: String): Account?
    fun findAccountByTenantAndPhone(tenant: Tenant, phone: String): Account?
}