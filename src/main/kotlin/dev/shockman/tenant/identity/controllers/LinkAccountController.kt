package dev.shockman.tenant.identity.controllers

import dev.shockman.tenant.identity.application.IdentityLinkService
import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.LinkRequestStatus
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant
import java.util.UUID

class LinkAccountController(
    private val session: HttpSession,
    private val identityLinkService: IdentityLinkService,
    private val identityProperties: IdentityProperties,
) {

    @GetMapping("/link-account")
    fun linkAccount(
        @RequestParam invite: UUID
    ): ResponseEntity<Void> {
        val state = UUID.randomUUID().toString()

        val inviteEntity = identityLinkService.getAccountLinkById(invite) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Identity link request not found"
        )

        val nonce = UUID.randomUUID().toString()

        session.setAttribute("inviteLinkState", state)
        session.setAttribute("inviteLinkId", inviteEntity.id)
        session.setAttribute("inviteLinkNonce", nonce)

        val keycloakUrl = UriComponentsBuilder
            .fromUriString(identityProperties.authUrl)
            .queryParam("client_id", identityProperties.clientId)
            .queryParam("response_type", "code")
            .queryParam("scope", identityProperties.scope)
            .queryParam("redirect_uri", identityLinkService.redirectUri)
            .queryParam("state", state)
            .queryParam("nonce", nonce)
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
        val nonce = session.getAttribute("inviteLinkNonce")

        if(sessionLinkState != state) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid state")
        }

        val tokens = identityLinkService.exchangeForCode(code, nonce?.toString())
        val idToken = tokens.idToken()
        val invite = identityLinkService.getInviteById(UUID.fromString(inviteLinkId.toString())) ?:
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid invite link")
        val expires = invite.expires

        if((expires != null && expires < Instant.now()) || invite.status != LinkRequestStatus.PENDING) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite is expired or already used")
        }

        val account = invite.account

        identityLinkService.linkIdentityToAccount(account, idToken)

        return ResponseEntity.ok().build()
    }
}