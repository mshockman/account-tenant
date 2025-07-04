package dev.shockman.controller

import dev.shockman.dto.CreateAccountRequest
import dev.shockman.dto.RealmTenantAccountResponse
import dev.shockman.dto.UpdateAccountRequest
import dev.shockman.dto.toRealmTenantAccount
import dev.shockman.service.RealmService
import dev.shockman.service.TenantService
import dev.shockman.service.AccountService
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
@RequestMapping("/realms/{realm}/tenants/{tenant}/accounts")
class RealmTenantAccountController(
    private val realmService: RealmService,
    private val tenantService: TenantService,
    private val accountService: AccountService
) {
    @PostMapping("")
    fun createTenantAccount(
        @PathVariable("realm") realmSlug: String,
        @PathVariable("tenant") tenantSlug: String,
        @RequestBody createAccountRequest: CreateAccountRequest
    ): RealmTenantAccountResponse {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        return accountService.create(tenant, createAccountRequest).toRealmTenantAccount()
    }

    @GetMapping("/{accountId}")
    fun getTenantAccountById(
        @PathVariable("realm") realmSlug: String,
        @PathVariable("tenant") tenantSlug: String,
        @PathVariable("accountId") accountId: UUID
    ): RealmTenantAccountResponse {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        return this@RealmTenantAccountController.accountService.findTenantAccountById(tenant, accountId).toRealmTenantAccount()
    }

    @PatchMapping("/{accountId}")
    fun updateTenantAccount(
        @PathVariable("realm") realmSlug: String,
        @PathVariable("tenant") tenantSlug: String,
        @PathVariable("accountId") accountId: UUID,
        @RequestBody updateAccountRequest: UpdateAccountRequest
    ): RealmTenantAccountResponse {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        val account = accountService.findTenantAccountById(tenant, accountId)
        return this@RealmTenantAccountController.accountService.update(account, updateAccountRequest).toRealmTenantAccount()
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTenantAccount(
        @PathVariable("realm") realmSlug: String,
        @PathVariable("tenant") tenantSlug: String,
        @PathVariable("accountId") accountId: UUID
    ) {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        val account = accountService.findTenantAccountById(tenant, accountId)
        return accountService.delete(account)
    }
}