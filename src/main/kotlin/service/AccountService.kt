package dev.shockman.service

import dev.shockman.dto.CreateAccountRequest
import dev.shockman.dto.UpdateAccountRequest
import dev.shockman.entity.Tenant
import dev.shockman.entity.Account
import dev.shockman.repository.AccountRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AccountService(val accountRepository: AccountRepository) {
    fun findTenantAccountByUsername(tenant: Tenant, username: String): Account? {
        return accountRepository.findAccountByTenantAndUsername(tenant, username) ?: throw EntityNotFoundException("Account not found.")
    }

    fun findTenantAccountByEmail(tenant: Tenant, email: String): Account? {
        return accountRepository.findAccountByTenantAndEmail(tenant, email) ?: throw EntityNotFoundException("Account not found.")
    }

    fun findTenantAccountById(tenant: Tenant, id: UUID): Account {
        return accountRepository.findAccountByTenantAndId(tenant, id) ?: throw EntityNotFoundException("Account not found.")
    }

    fun create(tenant: Tenant, account: CreateAccountRequest): Account {
        return accountRepository.save(
            Account(
                enabled = account.enabled,
                username = account.username,
                email = account.email,
                phone = account.phone,
                firstName = account.firstName,
                lastName = account.lastName,
                attributes = account.attributes,
                tenant = tenant
            )
        )
    }

    fun update(account: Account, updateAccountRequest: UpdateAccountRequest): Account {
        account.email = updateAccountRequest.email
        account.phone = updateAccountRequest.phone
        account.firstName = updateAccountRequest.firstName
        account.lastName = updateAccountRequest.lastName
        account.attributes = updateAccountRequest.attributes
        account.username = updateAccountRequest.username
        account.enabled = updateAccountRequest.enabled

        return accountRepository.save(account)
    }

    fun delete(account: Account) {
        return accountRepository.delete(account)
    }
}