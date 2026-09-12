package dev.shockman.tenant.realm.api.v1

import dev.shockman.tenant.realm.persistance.Realm
import java.time.Instant
import java.util.UUID

data class RealmResponse(val id: UUID, var name: String, val slug: String, val createdAt: Instant, var updatedAt: Instant)

fun Realm.toResponse(): RealmResponse {
    return RealmResponse(
        id = requireNotNull(id) { "id must not be null" },
        name = name,
        slug = slug,
        createdAt = requireNotNull(createdAt) { "createdAt must not be null" },
        updatedAt = requireNotNull(updatedAt) { "updatedAt must not be null" }
    )
}