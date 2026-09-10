package dev.shockman.tenant.identity.api.v1

import dev.shockman.tenant.accounts.application.AccountService
import dev.shockman.tenant.config.TenantServiceProperties
import dev.shockman.tenant.identity.application.IdentityLinkService
import dev.shockman.tenant.identity.config.IdentityProperties
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("")
class IdentityController(
    private val mailSender: JavaMailSender,
    private val session: HttpSession,
    private val accountService: AccountService,
    private val identityLinkService: IdentityLinkService,
    private val identityProperties: IdentityProperties,
    private val tenantServiceProperties: TenantServiceProperties
) {
    @GetMapping("/api/v1/accounts/{accountId}/invite")
    fun inviteUser(
        @PathVariable accountId: UUID
    ) {
        val account = accountService.getAccountById(accountId)
        val invite = identityLinkService.inviteAccountEmail(account)

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)

        helper.setSubject("Link Your Account")
        helper.setText("""
            <h1>Link Your Account</h1>
            <p>Click the link below to link your account to your email address.</p>
            <a href="${tenantServiceProperties.baseUrl}/link-account?invite=${invite.id}">Link Account</a>
        """.trimIndent(), true)
        helper.setFrom("no-reply@example.com")
        helper.setTo("user@example.com")
        mailSender.send(message)
    }

    @GetMapping("/link-account")
    fun linkAccount(
        @RequestParam invite: UUID
    ): ResponseEntity<Void> {
        val state = UUID.randomUUID().toString()

        val inviteEntity = identityLinkService.getAccountLinkById(invite) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Identity link request not found"
        )

        session.setAttribute("inviteLinkState", state)
        session.setAttribute("inviteLinkId", inviteEntity.id)

        val redirectUri = "${tenantServiceProperties.baseUrl}/link/callback"

        val keycloakUrl = UriComponentsBuilder
            .fromUriString(identityProperties.authUrl)
            .queryParam("client_id", identityProperties.clientId)
            .queryParam("response_type", "code")
            .queryParam("scope", "openid")
            .queryParam("redirect_uri", redirectUri)
            .queryParam("state", state)
            .build()
            .encode()
            .toUri()

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(keycloakUrl)
            .build()
    }

    @GetMapping("/link/callback")
    fun linkAccountCallback(
        @RequestParam state: String,
        @RequestParam code: String
    ): ResponseEntity<Void> {
        val sessionLinkState = session.getAttribute("inviteLinkState")
        val inviteLinkId = session.getAttribute("inviteLinkId")

        if(sessionLinkState != state) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid state")
        }

        val tokens = identityLinkService.exchangeForCode(code)

        return ResponseEntity.ok().build()
    }
}