package dev.shockman.tenant.identity.application

import dev.shockman.tenant.accounts.persistance.Account
import dev.shockman.tenant.config.TenantServiceProperties
import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.AccountIdentity
import dev.shockman.tenant.identity.persistance.AccountIdentityLink
import dev.shockman.tenant.identity.persistance.AccountIdentityLinkRequestRepository
import dev.shockman.tenant.identity.persistance.AccountIdentityRepository
import dev.shockman.tenant.identity.persistance.Identity
import dev.shockman.tenant.identity.persistance.LinkRequestStatus
import dev.shockman.tenant.identity.persistance.LinkRequestType
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtTimestampValidator
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.UUID

@Service
class IdentityLinkService(
    private val accountLinkRepository: AccountIdentityLinkRequestRepository,
    private val identityService: IdentityService,
    private val identityProperties: IdentityProperties,
    private val tenantServiceProperties: TenantServiceProperties,
    private val accountIdentityRepository: AccountIdentityRepository,
    private val mailSender: JavaMailSender,

) {
    private val restClient = RestClient.create()
    val redirectUri = "${tenantServiceProperties.baseUrl}/link/callback"

    @Transactional
    fun inviteAccountEmail(account: Account): AccountIdentityLink {
        val invite = accountLinkRepository.save(AccountIdentityLink(
            account = account,
            requestType = LinkRequestType.EMAIL,
            status = LinkRequestStatus.PENDING,
        ))

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

        return invite
    }

    @Transactional(readOnly = true)
    fun getInviteById(id: UUID): AccountIdentityLink? {
        return accountLinkRepository.findById(id).orElse(null)
    }

    @Transactional(readOnly = true)
    fun getAccountLinkById(id: UUID): AccountIdentityLink? {
        return accountLinkRepository.findById(id).orElse(null)
    }

    class TokenExchangeResponse(
        private val tokenResponse: TokenResponse,
        private val nonce: String?,
        private val identityProperties: IdentityProperties
    ) {
        private val decoder = NimbusJwtDecoder.withJwkSetUri(identityProperties.jwksUrl).build().apply {
            setJwtValidator(
                DelegatingOAuth2TokenValidator(
                    JwtValidators.createDefaultWithIssuer(identityProperties.issuer),
                    JwtTimestampValidator(),
                    { jwt ->
                        if(jwt.audience.contains(identityProperties.clientId)) OAuth2TokenValidatorResult.success()
                        else OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_audience"))
                    },
                    { jwt ->
                        if(jwt.claims["nonce"] == nonce) OAuth2TokenValidatorResult.success()
                        else OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_nonce"))
                    }
                )
            )
        }

        fun idToken(): Jwt = decoder.decode(tokenResponse.idToken)
        fun accessToken(): Jwt = decoder.decode(tokenResponse.accessToken)
    }

    fun exchangeForCode(code: String, nonce: String? = null): TokenExchangeResponse {
        val form = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("client_id", identityProperties.clientId)
            add("redirect_uri", redirectUri)

            // If your Keycloak client is confidential:
            add("client_secret", identityProperties.clientSecret)
        }

        val response = restClient.post()
            .uri(identityProperties.tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body<TokenResponse>()
            ?: throw IllegalStateException("Keycloak returned an empty token response")

        return TokenExchangeResponse(
            response,
            nonce,
            identityProperties
        )
    }

    @Transactional
    fun linkIdentityToAccount(account: Account, idToken: Jwt): AccountIdentity {
        val identity = identityService.getOrCreateIdentityFromIdToken(idToken)

        return try {
            val link = accountIdentityRepository.saveAndFlush(
                AccountIdentity(account = account, identity = identity)
            )

            account.identities.add(link)

            link
        } catch (e: DataIntegrityViolationException) {
            getIdentityLinkForAccountAndIdentity(account, identity)
                ?: throw e
        }
    }

    @Transactional(readOnly = true)
    fun getIdentityLinkForAccountAndIdentity(account: Account, identity: Identity): AccountIdentity? {
        return accountIdentityRepository.findByAccountAndIdentity(account, identity)
    }
}