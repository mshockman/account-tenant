package dev.shockman.tenant.tenant.api.v1

import dev.shockman.tenant.realm.application.RealmService
import dev.shockman.tenant.tenant.application.TenantService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/realms/by-slug/{realmSlug}/tenants/{tenantSlug}")
class RealmTenantSlugController(
    val realmService: RealmService,
    val tenantService: TenantService,
) {
    @GetMapping
    fun getRealmBySlug(
        @PathVariable realmSlug: String,
        @PathVariable tenantSlug: String
    ): TenantResponse {
        val realm = realmService.findBySlug(realmSlug)
        val tenant = tenantService.findByRealmAndSlug(realm, tenantSlug)
        return tenant.toResponse()
    }
}