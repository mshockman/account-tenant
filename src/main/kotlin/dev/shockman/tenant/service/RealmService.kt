package dev.shockman.tenant.service

import dev.shockman.messaging.message.storage.jdbc.postgres.OutboxService
import dev.shockman.tenant.dto.CreateRealmRequest
import dev.shockman.tenant.entity.Realm
import dev.shockman.tenant.api.messages.RealmCreatedEvent
import dev.shockman.tenant.api.messages.RealmDeletedEvent
import dev.shockman.tenant.repository.RealmRepository
import io.micrometer.tracing.Tracer
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RealmService(
    private val repository: RealmRepository,
    private val outboxService: OutboxService,
    private val tracer: Tracer
) {
    fun findBySlug(slug: String): Realm {
        return repository.findBySlug(slug) ?: throw EntityNotFoundException("Realm with that slug not found.")
    }

    fun findById(id: UUID): Realm {
        return repository.findById(id).orElseThrow { EntityNotFoundException("Realm with that id not found.") }
    }

    fun findAll(): List<Realm> {
        return repository.findAll()
    }

    @Transactional
    fun create(createRealm: CreateRealmRequest): Realm {
        val realm = repository.saveAndFlush(
            Realm(
                name = createRealm.name,
                slug = createRealm.slug
            )
        )

        outboxService.send(
            RealmCreatedEvent(
                realmId = realm.id,
                name = realm.name,
                slug = realm.slug,
                version = realm.version,
                createdAt = requireNotNull(realm.createdAt) { "Realm created at timestamp is null." },
                updatedAt = requireNotNull(realm.updatedAt) { "Realm updated at timestamp is null." }
            )
        )

        return realm
    }

    @Transactional
    fun delete(realm: Realm) {
        repository.delete(realm)

        outboxService.send(
            RealmDeletedEvent(
                realmId = realm.id,
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )
    }
}