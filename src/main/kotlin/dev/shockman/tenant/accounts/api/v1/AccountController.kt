package dev.shockman.tenant.accounts.api.v1

import dev.shockman.tenant.accounts.application.AccountService
import dev.shockman.tenant.tenant.application.TenantService
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
class AccountController(
    private val tenantService: TenantService,
    private val accountService: AccountService
) {
    @PostMapping("accounts")
    fun createTenantAccount(
        @RequestBody createAccountRequest: CreateAccountRequest
    ): RealmTenantAccountResponse {
        val tenant = tenantService.findById(createAccountRequest.tenantId)
        return accountService.create(tenant, createAccountRequest).toRealmTenantAccount()
    }

    @GetMapping("accounts/{accountId}")
    fun getTenantAccountById(
        @PathVariable accountId: UUID
    ): RealmTenantAccountResponse {
        return accountService.getAccountById(accountId).toRealmTenantAccount()
    }

    @PatchMapping("accounts/{accountId}")
    fun updateTenantAccount(
        @PathVariable accountId: UUID,
        @RequestBody updateAccountRequest: UpdateAccountRequest
    ): RealmTenantAccountResponse {
        val account = accountService.getAccountById(accountId)
        return this@AccountController.accountService.update(account, updateAccountRequest).toRealmTenantAccount()
    }

    @DeleteMapping("accounts/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTenantAccount(
        @PathVariable accountId: UUID
    ) {
        val account = accountService.getAccountById(accountId)
        return accountService.delete(account)
    }
}