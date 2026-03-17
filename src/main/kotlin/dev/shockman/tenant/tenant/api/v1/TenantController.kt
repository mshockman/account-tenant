package dev.shockman.tenant.tenant.api.v1

import dev.shockman.tenant.realm.application.RealmService
import dev.shockman.tenant.tenant.application.TenantService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/tenants")
class TenantController(
    private val tenantService: TenantService,
    private val realmService: RealmService,
) {
    @PostMapping("/create")
    fun createTenant(
        @RequestBody tenant: CreateTenantRequest
    ): TenantResponse {
        val realm = realmService.findById(tenant.realmId)

        return tenantService.create(realm, tenant).toResponse()
    }

    @GetMapping("/{id}")
    fun getTenant(
        @PathVariable id: UUID,
    ): TenantResponse {
        return tenantService.findById(id).toResponse()
    }

    @PatchMapping("/{id}")
    fun updateTenant(
        @PathVariable id: UUID,
        @RequestBody tenantUpdate: UpdateTenant
    ): TenantResponse {
        val tenant = tenantService.findById(id)
        return tenantService.update(tenant, tenantUpdate).toResponse()
    }

    @DeleteMapping("/{id}")
    fun deleteTenant(
        @PathVariable id: UUID,
    ) {
        val tenant = tenantService.findById(id)
        tenantService.delete(tenant)
    }
}