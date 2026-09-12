package dev.shockman.tenant.identity.application

import dev.shockman.messaging.message.storage.jdbc.postgres.OutboxService
import dev.shockman.tenant.api.messages.IdentityCreatedEvent
import dev.shockman.tenant.identity.config.IdentityProperties
import dev.shockman.tenant.identity.persistance.Identity
import dev.shockman.tenant.identity.persistance.IdentityRepository
import io.micrometer.tracing.Tracer
import jakarta.transaction.Transactional
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class IdentityService(
    private val identityRepository: IdentityRepository,
    private val identityProperties: IdentityProperties,
    private val outboxService: OutboxService,
    private val tracer: Tracer
) {
    @Transactional
    fun getOrCreateIdentityFromIdToken(idToken: Jwt): Identity {
        val issuer = idToken.issuer?.toString() ?: throw IllegalArgumentException("Issuer is null")
        val subject = idToken.subject ?: throw IllegalArgumentException("Subject is null")

        if(issuer != identityProperties.issuer) {
            error("Issuer does not match")
        }

        val email = idToken.claims["email"]?.toString()
        val emailVerified = idToken.claims["email_verified"]?.toString()?.toBoolean() ?: false
        val phone = idToken.claims["phone"]?.toString()
        val phoneVerified = idToken.claims["phone_verified"]?.toString()?.toBoolean() ?: false
        val username = idToken.claims["preferred_username"]?.toString()
            ?: idToken.getClaimAsString("email")
            ?: subject

        val created = identityRepository.upsert(username, subject, email, emailVerified, phone, phoneVerified)
        val identity = identityRepository.findBySubject(subject) ?: error("Identity not found")

        if(created > 0) {
            outboxService.send(IdentityCreatedEvent(
                id = requireNotNull(identity.id),
                username = identity.username,
                email = identity.email,
                emailVerified = identity.emailVerified,
                phone = identity.phone,
                phoneVerified = identity.phoneVerified,
                subject = identity.subject,
            ), correlationId = tracer.currentSpan()?.context()?.traceId())
        }

        return identity
    }
}