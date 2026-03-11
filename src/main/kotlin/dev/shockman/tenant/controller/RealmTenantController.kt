package dev.shockman.tenant.controller

import dev.shockman.tenant.dto.CreateTenantRequest
import dev.shockman.tenant.dto.TenantResponse
import dev.shockman.tenant.dto.UpdateTenant
import dev.shockman.tenant.dto.toResponse
import dev.shockman.tenant.service.RealmService
import dev.shockman.tenant.service.TenantService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/realms/{realm}")
class RealmTenantController(private val realmService: RealmService, private val tenantService: TenantService) {
    @GetMapping("/tenants/{tenantSlug}")
    fun getTenant(@PathVariable("realm") realmSlug: String, @PathVariable("tenantSlug") tenantSlug: String): TenantResponse {
        val realm = realmService.findBySlug(realmSlug)
        return tenantService.findByRealmAndSlug(realm, tenantSlug).toResponse()
    }

    @PostMapping("/tenants")
    fun createTenant(@PathVariable("realm") realmSlug: String, @RequestBody tenant: CreateTenantRequest): TenantResponse {
        val realm = realmService.findBySlug(realmSlug)
        return tenantService.create(realm, tenant).toResponse()
    }

    @PatchMapping("/tenants/{tenantSlug}")
    fun updateTenant(@PathVariable("realm") realmSlug: String, @PathVariable("tenantSlug") tenantSlug: String, @RequestBody updateTenant: UpdateTenant): TenantResponse {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        return tenantService.update(tenant, updateTenant).toResponse()
    }

    @DeleteMapping("/tenants/{tenantSlug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTenant(@PathVariable("realm") realmSlug: String, @PathVariable("tenantSlug") tenantSlug: String) {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        tenantService.delete(tenant)
    }
}