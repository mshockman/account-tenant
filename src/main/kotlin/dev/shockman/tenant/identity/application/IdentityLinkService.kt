package dev.shockman.tenant.identity.application

import dev.shockman.tenant.accounts.persistance.Account
import dev.shockman.tenant.config.TenantServiceProperties
import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.AccountIdentityLink
import dev.shockman.tenant.identity.persistance.LinkRequestStatus
import dev.shockman.tenant.identity.persistance.LinkRequestType
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.util.UUID

@Service
class IdentityLinkService(
    private val accountLinkRepository: AccountIdentityLinkRequestRepository,
    private val identityProperties: IdentityProperties,
    private val tenantServiceProperties: TenantServiceProperties
) {
    private val restClient = RestClient.create()
    public val redirectUri = "${tenantServiceProperties.baseUrl}/link/callback"

    @Transactional
    fun inviteAccountEmail(account: Account): AccountIdentityLink {
        val invite = AccountIdentityLink(
            account = account,
            requestType = LinkRequestType.EMAIL,
            status = LinkRequestStatus.PENDING,
        )

        return accountLinkRepository.save(invite)
    }

    @Transactional(readOnly = true)
    fun getAccountLinkById(id: UUID): AccountIdentityLink? {
        return accountLinkRepository.findById(id).orElse(null)
    }

    fun exchangeForCode(code: String): TokenResponse {
        val form = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("client_id", identityProperties.clientId)
            add("redirect_uri", redirectUri)

            // If your Keycloak client is confidential:
            add("client_secret", identityProperties.clientSecret)
        }

        return restClient.post()
            .uri(identityProperties.tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body<TokenResponse>()
            ?: throw IllegalStateException("Keycloak returned an empty token response")
    }
}