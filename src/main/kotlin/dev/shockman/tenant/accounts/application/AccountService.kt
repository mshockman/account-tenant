package dev.shockman.tenant.accounts.application

import dev.shockman.messaging.message.storage.jdbc.postgres.OutboxService
import dev.shockman.tenant.accounts.persistance.Account
import dev.shockman.tenant.accounts.persistance.AccountRepository
import dev.shockman.tenant.accounts.persistance.specifications.AccountFilterSpecification
import dev.shockman.tenant.api.messages.AccountCreatedEvent
import dev.shockman.tenant.api.messages.AccountDeletedEvent
import dev.shockman.tenant.api.messages.AccountUpdatedEvent
import dev.shockman.tenant.accounts.api.v1.CreateAccountRequest
import dev.shockman.tenant.accounts.api.v1.UpdateAccountRequest
import dev.shockman.tenant.realm.persistance.Realm
import dev.shockman.tenant.shared.applyIfNotNull
import dev.shockman.tenant.shared.toUUIDOrNull
import dev.shockman.tenant.tenant.application.MatchedCursor
import dev.shockman.tenant.tenant.persistance.Tenant
import io.micrometer.tracing.Tracer
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.jpa.domain.Specification
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

    fun getAccountById(accountId: UUID): Account {
        return accountRepository.findById(accountId).orElseThrow { EntityNotFoundException("Account not found.") }
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

    @Transactional(readOnly = true)
    fun searchAccounts(tenant: Tenant?, limit: Int?, query: String?, cursor: MatchedCursor?, realm: Realm?): List<Account> {
        val spec = getSearchAccountsSpec(tenant, query, cursor, realm, true)

        return accountRepository.findBy(spec) { q ->
            q.applyIfNotNull(limit) { q2, v ->
                q2.limit(v)
            }.all()
        }
    }

    fun countSearchAccounts(tenant: Tenant?, query: String?, realm: Realm?): Long {
        val spec = getSearchAccountsSpec(tenant, query, null, realm, false)

        return accountRepository.count(spec)
    }

    private fun getSearchAccountsSpec(tenant: Tenant?, query: String?, cursor: MatchedCursor?, realm: Realm?, fetchAssociations: Boolean): Specification<Account> {
        val filteredId = query?.toUUIDOrNull()

        val spec = (if (tenant != null) AccountFilterSpecification.byTenant(tenant) else AccountFilterSpecification.ordered())
            .applyIfNotNull(query) { spec, filter ->
                spec.and(AccountFilterSpecification.matchesFilter(filter, filteredId))
            }
            .applyIfNotNull(cursor) { spec, c ->
                spec.and(AccountFilterSpecification.afterCursorCreatedAt(c, filteredId))
            }.let { spec ->
                if (realm != null) spec.and(AccountFilterSpecification.byRealm(realm))
                else spec
            }.let { spec ->
                if(fetchAssociations) spec.and(AccountFilterSpecification.fetchTenantAndRealm())
                else spec
            }

        return spec
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