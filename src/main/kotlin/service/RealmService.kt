package dev.shockman.service

import dev.shockman.dto.CreateRealmRequest
import dev.shockman.entity.Realm
import dev.shockman.repository.RealmRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RealmService(private val repository: RealmRepository) {
    fun findBySlug(slug: String): Realm {
        return repository.findBySlug(slug) ?: throw EntityNotFoundException("Realm with that slug not found.")
    }

    fun findById(id: UUID): Realm {
        return repository.findById(id).orElseThrow { EntityNotFoundException("Realm with that id not found.") }
    }

    fun create(createRealm: CreateRealmRequest): Realm {
        return repository.save(Realm(
            name = createRealm.name,
            slug = createRealm.slug
        ))
    }

    fun delete(id: UUID) {
        repository.deleteById(id)
    }
}