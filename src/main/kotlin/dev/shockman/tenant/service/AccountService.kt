package dev.shockman.tenant.service

import dev.shockman.messaging.message.storage.jdbc.postgres.OutboxService
import dev.shockman.tenant.dto.CreateAccountRequest
import dev.shockman.tenant.dto.UpdateAccountRequest
import dev.shockman.tenant.entity.Account
import dev.shockman.tenant.entity.Tenant
import dev.shockman.tenant.api.messages.AccountCreatedEvent
import dev.shockman.tenant.api.messages.AccountDeletedEvent
import dev.shockman.tenant.api.messages.AccountUpdatedEvent
import dev.shockman.tenant.repository.AccountRepository
import io.micrometer.tracing.Tracer
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val outboxService: OutboxService,
    private val tracer: Tracer
) {
    fun findTenantAccountByUsername(tenant: Tenant, username: String): Account? {
        return accountRepository.findAccountByTenantAndUsername(tenant, username) ?: throw EntityNotFoundException("Account not found.")
    }

    fun findTenantAccountByEmail(tenant: Tenant, email: String): Account? {
        return accountRepository.findAccountByTenantAndEmail(tenant, email) ?: throw EntityNotFoundException("Account not found.")
    }

    fun findTenantAccountById(tenant: Tenant, id: UUID): Account {
        return accountRepository.findAccountByTenantAndId(tenant, id) ?: throw EntityNotFoundException("Account not found.")
    }

    @Transactional
    fun create(tenant: Tenant, account: CreateAccountRequest): Account {
        val account = accountRepository.saveAndFlush(
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

        outboxService.send(
            AccountCreatedEvent(
                realmId = account.tenant.realm.id,
                tenantId = account.tenant.id,
                accountId = account.id,
                enabled = account.enabled,
                firstName = account.firstName,
                lastName = account.lastName,
                email = account.email,
                phone = account.phone,
                username = account.username,
                remoteId = account.remoteId,
                attributes = account.attributes,
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )

        return account
    }

    @Transactional
    fun update(account: Account, updateAccountRequest: UpdateAccountRequest): Account {
        account.email = updateAccountRequest.email
        account.phone = updateAccountRequest.phone
        account.firstName = updateAccountRequest.firstName
        account.lastName = updateAccountRequest.lastName
        account.attributes = updateAccountRequest.attributes
        account.username = updateAccountRequest.username
        account.enabled = updateAccountRequest.enabled

        val updatedAccount = accountRepository.saveAndFlush(account)

        outboxService.send(
            AccountUpdatedEvent(
                realmId = updatedAccount.tenant.realm.id,
                tenantId = updatedAccount.tenant.id,
                accountId = updatedAccount.id,
                enabled = updatedAccount.enabled,
                firstName = updatedAccount.firstName,
                lastName = updatedAccount.lastName,
                email = updatedAccount.email,
                phone = updatedAccount.phone,
                username = updatedAccount.username,
                remoteId = updatedAccount.remoteId,
                attributes = updatedAccount.attributes,
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )

        return updatedAccount
    }

    @Transactional
    fun delete(account: Account) {
        accountRepository.delete(account)

        outboxService.send(
            message = AccountDeletedEvent(
                realmId = account.tenant.realm.id,
                tenantId = account.tenant.id,
                accountId = account.id,
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )
    }
}