package dev.shockman.tenant.accounts.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import dev.shockman.tenant.accounts.application.AccountService
import dev.shockman.tenant.realm.application.RealmService
import dev.shockman.tenant.shared.toUUIDOrNull
import dev.shockman.tenant.tenant.application.MatchedCursor
import dev.shockman.tenant.tenant.application.TenantService
import dev.shockman.tenant.tenant.application.toCursorString
import dev.shockman.tenant.tenant.application.toMatchedCursor
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/accounts")
class AccountAPIController(
    private val tenantService: TenantService,
    private val accountService: AccountService,
    private val objectMapper: ObjectMapper,
    private val realmService: RealmService
) {
    @PostMapping("")
    fun createTenantAccount(
        @Valid @RequestBody createAccountRequest: CreateAccountRequest
    ): RealmTenantAccountResponse {
        val tenant = tenantService.findById(createAccountRequest.tenantId)
        return accountService.create(tenant, createAccountRequest).toRealmTenantAccount()
    }

    @GetMapping("/{accountId}")
    fun getTenantAccountById(
        @PathVariable accountId: UUID
    ): RealmTenantAccountResponse {
        return accountService.getAccountById(accountId).toRealmTenantAccount()
    }

    @PatchMapping("/{accountId}")
    fun updateTenantAccount(
        @PathVariable accountId: UUID,
        @RequestBody updateAccountRequest: UpdateAccountRequest
    ): RealmTenantAccountResponse {
        val account = accountService.getAccountById(accountId)
        return this@AccountAPIController.accountService.update(account, updateAccountRequest).toRealmTenantAccount()
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTenantAccount(
        @PathVariable accountId: UUID
    ) {
        val account = accountService.getAccountById(accountId)
        return accountService.delete(account)
    }

    @PostMapping("/search")
    fun searchAccounts(@RequestBody searchAccountRequest: SearchAccountRequest): SearchAccountResponse {
        val tenant = searchAccountRequest.tenantId?.let { tenantService.findById(it) }

        val filterId = searchAccountRequest.query?.toUUIDOrNull()

        val cursor = searchAccountRequest.cursor?.toMatchedCursor(objectMapper)

        val realm = searchAccountRequest.realmId?.let { realmService.findById(it) }

        val accounts = accountService.searchAccounts(
            tenant,
            searchAccountRequest.limit + 1,
            searchAccountRequest.query,
            cursor,
            realm=realm
        ).map { it.toRealmTenantAccount() }.toMutableList()

        val nextCursor = if (accounts.size > searchAccountRequest.limit) {
            accounts.removeLast()

            accounts.lastOrNull()?.let {
                MatchedCursor(
                    when (filterId) {
                        null -> null
                        it.id -> 0
                        else -> 1
                    },
                    it.createdAt,
                    it.id
                )
            }
        } else null

        val count = accountService.countSearchAccounts(tenant, searchAccountRequest.query, realm=realm)

        return SearchAccountResponse(
            accounts,
            tenant?.id,
            realm?.id,
            nextCursor?.toCursorString(objectMapper),
            count
        )
    }
}