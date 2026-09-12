package dev.shockman.tenant.tenant.application

import dev.shockman.messaging.message.storage.jdbc.postgres.OutboxService
import dev.shockman.tenant.api.messages.TenantCreatedEvent
import dev.shockman.tenant.api.messages.TenantDeletedEvent
import dev.shockman.tenant.api.messages.TenantUpdatedEvent
import dev.shockman.tenant.tenant.api.v1.CreateTenantRequest
import dev.shockman.tenant.tenant.api.v1.UpdateTenant
import dev.shockman.tenant.realm.persistance.Realm
import dev.shockman.tenant.shared.applyIfNotNull
import dev.shockman.tenant.shared.toUUIDOrNull
import dev.shockman.tenant.tenant.persistance.Tenant
import dev.shockman.tenant.tenant.persistance.TenantRepository
import dev.shockman.tenant.tenant.persistance.specifications.TenantFilterSpecification
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

    fun findByIdWithRealm(id: UUID): Tenant {
        return tenantRepository.findByIdWithRealm(id) ?: throw EntityNotFoundException("Tenant not found.")
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
                realmId = requireNotNull(tenant.realm.id) { "Realm id is required" },
                tenantId = requireNotNull(tenant.id) { "Tenant id is required" },
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
                realmId = requireNotNull(tenant.realm.id) { "Realm id is required" },
                tenantId = requireNotNull(tenant.id) { "Tenant id is required" },
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
                realmId = requireNotNull(newTenant.realm.id) { "Realm id is required" },
                tenantId = requireNotNull(newTenant.id) { "Tenant id is required" },
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

    fun countByRealm(realm: Realm, query: String?): Long {
        val filteredId = query?.toUUIDOrNull()

        val spec = TenantFilterSpecification.byRealm(realm).applyIfNotNull(query) { spec, filter ->
            spec.and(TenantFilterSpecification.matchesFilter(filter, filteredId))
        }

        return tenantRepository.count(spec)
    }

    fun searchRealmTenants(realm: Realm, limit: Int?, query: String?, cursor: MatchedCursor?): List<Tenant> {
        val filteredId = query?.toUUIDOrNull()

        val spec = TenantFilterSpecification.byRealm(realm).applyIfNotNull(query) { spec, filter ->
            spec.and(TenantFilterSpecification.matchesFilter(filter, filteredId))
        }.applyIfNotNull(cursor) { spec, cursor ->
            spec.and(
                TenantFilterSpecification.afterCursorCreatedAt(
                    cursor = cursor,
                    filterId = filteredId
                )
            )
        }

        return tenantRepository.findBy(spec) { q ->
            q.applyIfNotNull(limit) { q2, v ->
                q2.limit(v)
            }.all()
        }
    }
}