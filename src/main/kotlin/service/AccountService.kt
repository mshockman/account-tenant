package dev.shockman.service

import dev.shockman.dto.CreateAccountRequest
import dev.shockman.dto.UpdateAccountRequest
import dev.shockman.entity.Tenant
import dev.shockman.entity.Account
import dev.shockman.messages.AccountCreatedEvent
import dev.shockman.repository.AccountRepository
import io.micrometer.tracing.Tracer
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val outboxService: OutboxService,
    private val clock: Clock,
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
            account.id.toString(),
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
                occurredAt = clock.instant(),
                createdAt = requireNotNull(account.createdAt) { "Account created at timestamp is null." },
                updatedAt = requireNotNull(account.updatedAt) { "Account updated at timestamp is null." },
                version = account.version,
                correlationId = tracer.currentSpan()?.context()?.traceId(),
            )
        )

        return account
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