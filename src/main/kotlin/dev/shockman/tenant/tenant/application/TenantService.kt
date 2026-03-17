package dev.shockman.tenant.tenant.application

import dev.shockman.messaging.message.storage.jdbc.postgres.OutboxService
import dev.shockman.tenant.api.messages.TenantCreatedEvent
import dev.shockman.tenant.api.messages.TenantDeletedEvent
import dev.shockman.tenant.api.messages.TenantUpdatedEvent
import dev.shockman.tenant.tenant.api.v1.CreateTenantRequest
import dev.shockman.tenant.tenant.api.v1.UpdateTenant
import dev.shockman.tenant.realm.persistance.Realm
import dev.shockman.tenant.tenant.persistance.Tenant
import dev.shockman.tenant.tenant.persistance.TenantRepository
import io.micrometer.tracing.Tracer
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TenantService(
    private val tenantRepository: TenantRepository,
    private val outboxService: OutboxService,
    private val tracer: Tracer
) {
    fun findByRealmAndId(realm: Realm, id: UUID): Tenant {
        return tenantRepository.findByRealmAndId(realm, id) ?: throw EntityNotFoundException("Tenant not found")
    }

    fun findByRealmAndSlug(realm: Realm, slug: String): Tenant {
        return tenantRepository.findByRealmAndSlug(realm, slug) ?: throw EntityNotFoundException("Tenant not found.")
    }

    fun findById(id: UUID): Tenant {
        return tenantRepository.findById(id).orElseThrow { EntityNotFoundException("Tenant not found.") }
    }

    @Transactional
    fun create(realm: Realm, createdTenant: CreateTenantRequest): Tenant {
        val tenant = tenantRepository.saveAndFlush(
            Tenant(
                slug = createdTenant.slug,
                name = createdTenant.name,
                realm = realm,
                enabled = createdTenant.enabled
            )
        )

        outboxService.send(
            TenantCreatedEvent(
                realmId = tenant.realm.id,
                tenantId = tenant.id,
                name = tenant.name,
                slug = tenant.slug,
                enabled = tenant.enabled,
                version = tenant.version,
                createdAt = requireNotNull(tenant.createdAt) { "Tenant created at timestamp is null." },
                updatedAt = requireNotNull(tenant.updatedAt) { "Tenant updated at timestamp is null." },
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )

        return tenant
    }

    @Transactional
    fun delete(tenant: Tenant) {
        tenantRepository.delete(tenant)

        outboxService.send(
            TenantDeletedEvent(
                realmId = tenant.realm.id,
                tenantId = tenant.id,
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )
    }

    @Transactional
    fun update(tenant: Tenant, updatedTenant: UpdateTenant): Tenant {
        tenant.enabled = updatedTenant.enabled ?: tenant.enabled
        tenant.name = updatedTenant.name ?: tenant.name
        val newTenant = tenantRepository.saveAndFlush(tenant)

        outboxService.send(
            TenantUpdatedEvent(
                realmId = newTenant.realm.id,
                tenantId = newTenant.id,
                name = newTenant.name,
                slug = newTenant.slug,
                enabled = newTenant.enabled,
                version = newTenant.version,
                createdAt = requireNotNull(newTenant.createdAt) { "Tenant created at timestamp is null." },
                updatedAt = requireNotNull(newTenant.updatedAt) { "Tenant updated at timestamp is null." },
            ),
            correlationId = tracer.currentSpan()?.context()?.traceId()
        )

        return newTenant
    }

    fun countByRealm(realm: Realm): Long {
        return tenantRepository.countByRealm(realm)
    }

//    fun findRealmTenants(realm: Realm, limit: Int?, filter: String?, after: After? = null): List<Tenant> {
//        val filteredId = filter?.let {
//            try {
//                UUID.fromString(it)
//            } catch (e: IllegalArgumentException) {
//                null
//            }
//        }
//
//        val spec = TenantFilterSpecification.byRealm(realm).applyIfNotNull(filter) { spec, filter ->
//            spec.and(TenantFilterSpecification.matchesFilter(filter, filteredId))
//        }.applyIfNotNull(after) { spec, after ->
//            spec.and(
//                TenantFilterSpecification.afterCursor(
//                createdAt = after.createdAt,
//                afterId = after.id
//            ))
//        }
//
//        return tenantRepository.findBy(spec) { q ->
//            q.sortBy(
//                Sort.by(
//                    Sort.Order.asc("createdAt"),
//                    Sort.Order.asc("id"),
//                )
//            ).applyIfNotNull(limit) { q2, v ->
//                q2.limit(v)
//            }.all()
//        }
//    }
}