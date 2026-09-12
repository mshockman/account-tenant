package dev.shockman.tenant.identity.controllers

import dev.shockman.tenant.identity.application.IdentityLinkService
import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.LinkRequestStatus
import jakarta.servlet.http.HttpSession
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant
import java.util.UUID

@Controller
class LinkAccountController(
    private val session: HttpSession,
    private val identityLinkService: IdentityLinkService,
    private val identityProperties: IdentityProperties,
) {
    companion object {
        private val INVITE_LINK_STATE = "inviteLinkState"
        private val INVITE_LINK_ID = "inviteLinkId"
        private val INVITE_LINK_NONCE = "inviteLinkNonce"
        private val INVITE_LINK_EXPIRES = "inviteLinkExpires"
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
        val expires = inviteEntity.expires

        if((expires != null && expires < Instant.now()) || inviteEntity.status != LinkRequestStatus.PENDING) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite is expired or already used")
        }

        val nonce = UUID.randomUUID().toString()

        session.setAttribute(INVITE_LINK_STATE, state)
        session.setAttribute(INVITE_LINK_ID, inviteEntity.id)
        session.setAttribute(INVITE_LINK_NONCE, nonce)
        session.setAttribute(INVITE_LINK_EXPIRES, Instant.now() + identityProperties.inviteLifespan)

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
        @RequestParam code: String,
        model: Model
    ): String {
        val sessionLinkState = session.getAttribute(INVITE_LINK_STATE)
        val inviteLinkId = session.getAttribute(INVITE_LINK_ID)
        val nonce = session.getAttribute(INVITE_LINK_NONCE)

        if(sessionLinkState != state) {
            return "link-account-error"
        }

        val tokens = identityLinkService.exchangeForCode(code, nonce?.toString())
        val idToken = tokens.idToken()
        val invite = identityLinkService.getInviteById(UUID.fromString(inviteLinkId.toString())) ?: return "link-account-error"
        val expires = session.getAttribute(INVITE_LINK_EXPIRES) as? Instant
        clearLinkSessionAttributes()

        if(expires == null || expires < Instant.now() || invite.status != LinkRequestStatus.PENDING) {
            model.addAttribute("error", "Invite is expired or already used.")
            return "link-account-error"
        }

        val account = invite.account

        val link = identityLinkService.linkIdentityToAccount(account, idToken)

        if(link == null) {
            model.addAttribute("error", "Identity already linked to account.")
            return "link-account-error"
        } else {
            identityLinkService.accept(invite)
            return "link-account-success"
        }
    }

    private fun clearLinkSessionAttributes() {
        session.removeAttribute(INVITE_LINK_EXPIRES)
        session.removeAttribute(INVITE_LINK_ID)
        session.removeAttribute(INVITE_LINK_NONCE)
        session.removeAttribute(INVITE_LINK_STATE)
    }
}