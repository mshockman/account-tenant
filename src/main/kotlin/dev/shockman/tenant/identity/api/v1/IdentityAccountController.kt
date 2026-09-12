package dev.shockman.tenant.identity.api.v1

import dev.shockman.tenant.accounts.application.AccountService
import dev.shockman.tenant.identity.application.IdentityLinkService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/accounts")
class IdentityAccountController(
    private val accountService: AccountService,
    private val identityLinkService: IdentityLinkService,
) {
    @GetMapping("/{accountId}/invite")
    fun inviteUser(
        @PathVariable accountId: UUID
    ) {
        identityLinkService.inviteAccountEmail(
            accountService.getAccountById(accountId)
        )
    }
}