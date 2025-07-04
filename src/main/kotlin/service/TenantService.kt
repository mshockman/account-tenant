package dev.shockman.service

import dev.shockman.dto.CreateTenantRequest
import dev.shockman.dto.UpdateTenant
import dev.shockman.entity.Realm
import dev.shockman.entity.Tenant
import dev.shockman.repository.TenantRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TenantService(private val tenantRepository: TenantRepository) {
    fun findByRealmAndId(realm: Realm, id: UUID): Tenant {
        return tenantRepository.findByRealmAndId(realm, id) ?: throw EntityNotFoundException("Tenant not found")
    }

    fun findByRealmAndSlug(realm: Realm, slug: String): Tenant {
        return tenantRepository.findByRealmAndSlug(realm, slug) ?: throw EntityNotFoundException("Tenant not found.")
    }

    fun create(realm: Realm, createdTenant: CreateTenantRequest): Tenant {
        return tenantRepository.save(Tenant(
            slug = createdTenant.slug,
            name = createdTenant.name,
            realm = realm,
            enabled = createdTenant.enabled
        ))
    }

    fun delete(tenant: Tenant) {
        tenantRepository.delete(tenant)
    }

    fun update(tenant: Tenant, updatedTenant: UpdateTenant): Tenant {
        tenant.enabled = updatedTenant.enabled ?: tenant.enabled
        tenant.name = updatedTenant.name ?: tenant.name
        return tenantRepository.save(tenant)
    }
}