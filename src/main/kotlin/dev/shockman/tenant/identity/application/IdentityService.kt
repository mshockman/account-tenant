package dev.shockman.tenant.identity.application

import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.Identity
import dev.shockman.tenant.identity.persistance.IdentityRepository
import jakarta.transaction.Transactional
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class IdentityService(
    private val identityRepository: IdentityRepository,
    private val IdentityProperties: IdentityProperties
) {
    @Transactional
    fun getOrCreateIdentityFromIdToken(idToken: Jwt): Identity {
        val issuer = idToken.issuer?.toString() ?: throw IllegalArgumentException("Issuer is null")
        val subject = idToken.subject ?: throw IllegalArgumentException("Subject is null")

        if(issuer != IdentityProperties.issuer) {
            error("Issuer does not match")
        }

        val email = idToken.claims["email"]?.toString()
        val emailVerified = idToken.claims["email_verified"]?.toString()?.toBoolean() ?: false
        val phone = idToken.claims["phone"]?.toString()
        val phoneVerified = idToken.claims["phone_verified"]?.toString()?.toBoolean() ?: false
        val username = idToken.claims["preferred_username"]?.toString()
            ?: idToken.getClaimAsString("email")
            ?: subject

        identityRepository.upsert(username, subject, email, emailVerified, phone, phoneVerified)

        return identityRepository.findBySubject(subject) ?: error("Identity not found")
    }
}