package dev.shockman.dto

import dev.shockman.entity.Realm
import java.time.Instant
import java.util.UUID

data class CreateRealmRequest(val name: String, val slug: String)

data class RealmResponse(val id: UUID, var name: String, val slug: String, val createdAt: Instant, var updatedAt: Instant)

fun Realm.toResponse(): RealmResponse {
    requireNotNull(createdAt) { "createdAt must not be null" }
    requireNotNull(updatedAt) { "updatedAt must not be null" }

    return RealmResponse(
        id = id,
        name = name,
        slug = slug,
        createdAt = requireNotNull(createdAt) { "createdAt must not be null" },
        updatedAt = requireNotNull(updatedAt) { "updatedAt must not be null" }
    )
}
