package dev.shockman.tenant.accounts.persistance

import dev.shockman.tenant.tenant.persistance.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface AccountRepository : JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    fun findAccountByTenantAndId(tenant: Tenant, id: UUID): Account?
    fun findAccountByTenantAndUsername(tenant: Tenant, username: String): Account?
    fun findAccountByTenantAndEmail(tenant: Tenant, email: String): Account?
    fun findAccountByTenantAndPhone(tenant: Tenant, phone: String): Account?
}