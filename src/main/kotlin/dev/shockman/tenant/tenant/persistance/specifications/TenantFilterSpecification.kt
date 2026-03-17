package dev.shockman.tenant.tenant.persistance.specifications

import dev.shockman.tenant.realm.persistance.Realm
import dev.shockman.tenant.tenant.persistance.Tenant
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.UUID
import java.time.Instant

object TenantFilterSpecification {
    fun byRealm(realm: Realm): Specification<Tenant> {
        return Specification { root, _, cb ->
            cb.equal(root.get<Realm>("realm"), realm)
        }
    }

    fun matchesFilter(filter: String, parsedId: UUID?): Specification<Tenant> {
        val likeValue = "%${filter.lowercase()}%"

        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>(
                cb.like(cb.lower(root.get("name")), likeValue),
                cb.like(cb.lower(root.get("slug")), likeValue),
            )

            if(parsedId != null) {
                predicates.add(cb.equal(root.get<UUID>("id"), parsedId))
            }

            cb.or(*predicates.toTypedArray())
        }
    }

    fun afterCursor(createdAt: Instant, afterId: UUID): Specification<Tenant> {
        return Specification { root, _, cb ->
            cb.or(
                cb.greaterThan(root.get("createdAt"), createdAt),
                cb.and(
                    cb.equal(root.get<Instant>("createdAt"), createdAt),
                    cb.greaterThan(root.get("id"), afterId)
                )
            )
        }
    }
}