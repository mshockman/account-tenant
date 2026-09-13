package dev.shockman.tenant.tenant.persistance.specifications

import dev.shockman.tenant.realm.persistance.Realm
import dev.shockman.tenant.tenant.application.MatchedCursor
import dev.shockman.tenant.tenant.persistance.Tenant
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.UUID
import java.time.Instant

object TenantFilterSpecification {
    fun byRealm(realm: Realm): Specification<Tenant> {
        return Specification { root, query, cb ->
            val idColumn = root.get<UUID>("id")
            val createdAtColumn = root.get<Instant>("createdAt")

            query?.orderBy(
                cb.asc(createdAtColumn),
                cb.asc(idColumn)
            )

            cb.equal(root.get<Realm>("realm"), realm)
        }
    }

    fun matchesFilter(filter: String, parsedId: UUID?): Specification<Tenant> {
        val likeValue = "%${filter.lowercase()}%"

        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>(
                cb.like(cb.lower(root.get("name")), likeValue),
            )

            if(parsedId != null) {
                predicates.add(cb.equal(root.get<UUID>("id"), parsedId))
            }

            cb.or(*predicates.toTypedArray())
        }
    }

    fun afterCursorCreatedAt(cursor: MatchedCursor, filterId: UUID?): Specification<Tenant> {
        return if(cursor.rank != null) {
            afterCursorCreatedAt(
                requireNotNull(filterId) { "Filter id is required to use after cursor with rank." },
                cursor.rank,
                cursor.after,
                cursor.id)
        } else {
            afterCursorCreatedAt(cursor.after, cursor.id)
        }
    }

    private fun afterCursorCreatedAt(afterCreatedAt: Instant, id: UUID): Specification<Tenant> {
        return Specification { root, query, cb ->
            val idColumn = root.get<UUID>("id")
            val createdAtColumn = root.get<Instant>("createdAt")

            query?.orderBy(
                cb.asc(createdAtColumn),
                cb.asc(idColumn)
            )

            cb.or(
                cb.greaterThan(createdAtColumn, afterCreatedAt),
                cb.and(
                    cb.equal(createdAtColumn, afterCreatedAt),
                    cb.greaterThan(idColumn, id)
                )
            )
        }
    }

    private fun afterCursorCreatedAt(filterId: UUID, rank: Int, afterCreatedAt: Instant, afterId: UUID): Specification<Tenant> {
        return Specification { root, query, cb ->
            val idColumn = root.get<UUID>("id")
            val createdAtColumn = root.get<Instant>("createdAt")

            val rankColumn = cb.selectCase<Int>()
                .`when`(cb.equal(idColumn, filterId), 0)
                .otherwise(1)

            query?.orderBy(
                cb.asc(rankColumn),
                cb.asc(createdAtColumn),
                cb.asc(idColumn)
            )

            cb.or(
                cb.greaterThan(rankColumn, rank),
                cb.and(
                    cb.equal(rankColumn, rank),
                    cb.greaterThan(createdAtColumn, afterCreatedAt)
                ),
                cb.and(
                    cb.equal(rankColumn, rank),
                    cb.equal(createdAtColumn, afterCreatedAt),
                    cb.greaterThan(idColumn, afterId)
                )
            )
        }
    }
}