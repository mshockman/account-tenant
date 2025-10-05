package dev.shockman.tenant.service

import dev.shockman.tenant.dto.CreateTenantRequest
import dev.shockman.tenant.dto.UpdateTenant
import dev.shockman.tenant.entity.Realm
import dev.shockman.tenant.entity.Tenant
import dev.shockman.tenant.messages.TenantCreatedEvent
import dev.shockman.tenant.messages.TenantDeletedEvent
import dev.shockman.tenant.messages.TenantUpdatedEvent
import dev.shockman.tenant.repository.TenantRepository
import io.micrometer.tracing.Tracer
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.UUID

@Service
class TenantService(
    private val tenantRepository: TenantRepository,
    private val outboxService: OutboxService,
    private val clock: Clock,
    private val tracer: Tracer
) {
    fun findByRealmAndId(realm: Realm, id: UUID): Tenant {
        return tenantRepository.findByRealmAndId(realm, id) ?: throw EntityNotFoundException("Tenant not found")
    }

    fun findByRealmAndSlug(realm: Realm, slug: String): Tenant {
        return tenantRepository.findByRealmAndSlug(realm, slug) ?: throw EntityNotFoundException("Tenant not found.")
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
            tenant.id.toString(),
            TenantCreatedEvent(
                realmId = tenant.realm.id,
                tenantId = tenant.id,
                name = tenant.name,
                slug = tenant.slug,
                enabled = tenant.enabled,
                version = tenant.version,
                createdAt = requireNotNull(tenant.createdAt) { "Tenant created at timestamp is null." },
                updatedAt = requireNotNull(tenant.updatedAt) { "Tenant updated at timestamp is null." },
                occurredAt = clock.instant(),
                correlationId = tracer.currentSpan()?.context()?.traceId(),
            )
        )

        return tenant
    }

    @Transactional
    fun delete(tenant: Tenant) {
        tenantRepository.delete(tenant)

        outboxService.send(
            tenant.id.toString(),
            TenantDeletedEvent(
                realmId = tenant.realm.id,
                tenantId = tenant.id,
                occurredAt = clock.instant(),
                correlationId = tracer.currentSpan()?.context()?.traceId(),
            )
        )
    }

    @Transactional
    fun update(tenant: Tenant, updatedTenant: UpdateTenant): Tenant {
        tenant.enabled = updatedTenant.enabled ?: tenant.enabled
        tenant.name = updatedTenant.name ?: tenant.name
        val newTenant = tenantRepository.saveAndFlush(tenant)

        outboxService.send(
            newTenant.id.toString(),
            TenantUpdatedEvent(
                realmId = newTenant.realm.id,
                tenantId = newTenant.id,
                name = newTenant.name,
                slug = newTenant.slug,
                enabled = newTenant.enabled,
                version = newTenant.version,
                createdAt = requireNotNull(newTenant.createdAt) { "Tenant created at timestamp is null." },
                updatedAt = requireNotNull(newTenant.updatedAt) { "Tenant updated at timestamp is null." },
                occurredAt = clock.instant(),
                correlationId = tracer.currentSpan()?.context()?.traceId(),
            )
        )

        return newTenant
    }
}