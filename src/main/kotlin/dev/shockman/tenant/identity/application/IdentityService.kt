package dev.shockman.tenant.identity.application

import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.Identity
import dev.shockman.tenant.identity.persistance.IdentityRepository
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.Instant

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
            throw IllegalArgumentException("Issuer does not match")
        }

        identityRepository.findBySubject(subject)?.let {
            val identity = updateIdentityClaimsFromIdTokenWithoutSave(idToken, it)
            return identityRepository.saveAndFlush(identity)
        }

        return try {
            createIdentityFromIdTokenAndFlush(idToken)
        } catch(ex: DataIntegrityViolationException) {
            identityRepository.findBySubject(subject) ?: throw ex
        }
    }

    @Transactional
    fun updateIdentityClaimsFromIdTokenWithoutSave(idToken: Jwt, identity: Identity? = null): Identity {
        val issuer = idToken.issuer?.toString() ?: throw IllegalArgumentException("Issuer is null")
        val subject = idToken.subject ?: throw IllegalArgumentException("Subject is null")
        val username = idToken.claims["preferred_username"]?.toString()
            ?: idToken.getClaimAsString("email")
            ?: subject

        if(issuer != IdentityProperties.issuer) {
            throw IllegalArgumentException("Issuer does not match")
        }

        val updatedIdentity = identity ?: Identity(
            subject = subject,
            username = username
        )

        updatedIdentity.email = idToken.claims["email"]?.toString()
        updatedIdentity.emailVerified = idToken.claims["email_verified"]?.toString()?.toBoolean() ?: false
        updatedIdentity.phone = idToken.claims["phone"]?.toString()
        updatedIdentity.phoneVerified = idToken.claims["phone_verified"]?.toString()?.toBoolean() ?: false
        updatedIdentity.username = username

//        if(updatedIdentity.createdAt == null) updatedIdentity.createdAt = Instant.now()
//        if(updatedIdentity.updatedAt == null) updatedIdentity.updatedAt = Instant.now()

        return updatedIdentity
    }

    @Transactional
    fun createIdentityFromIdTokenAndFlush(idToken: Jwt): Identity {
        val identity = updateIdentityClaimsFromIdTokenWithoutSave(idToken)
        return identityRepository.saveAndFlush(identity)
    }
}