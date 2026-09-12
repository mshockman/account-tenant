package dev.shockman.tenant.tenant.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import dev.shockman.tenant.realm.application.RealmService
import dev.shockman.tenant.shared.toUUIDOrNull
import dev.shockman.tenant.tenant.application.MatchedCursor
import dev.shockman.tenant.tenant.application.TenantService
import dev.shockman.tenant.tenant.application.toCursorString
import dev.shockman.tenant.tenant.application.toMatchedCursor
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
    private val objectMapper: ObjectMapper
) {
    @PostMapping("/create")
    fun createTenant(
        @RequestBody tenant: CreateTenantRequest
    ): TenantResponse {
        val realm = realmService.findById(tenant.realmId)

        return tenantService.create(realm, tenant).toResponse()
    }

    @GetMapping("/ids/{id}")
    fun getTenant(
        @PathVariable id: UUID,
    ): TenantResponse {
        return tenantService.findByIdWithRealm(id).toResponse()
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

    @PostMapping("/search")
    fun searchRealmTenants(@RequestBody searchRealmTenantRequest: SearchRealmTenantRequest): SearchRealmTenantResponse {
        val realm = realmService.findById(searchRealmTenantRequest.realmId)

        val filterId = searchRealmTenantRequest.query?.toUUIDOrNull()

        val cursor = searchRealmTenantRequest.cursor?.toMatchedCursor(objectMapper)

        val tenants = tenantService.searchRealmTenants(
            realm,
            searchRealmTenantRequest.limit + 1,
            searchRealmTenantRequest.query,
            cursor
        ).map { it.toResponse() }.toMutableList()

        val nextCursor = if(tenants.size > searchRealmTenantRequest.limit) {
            tenants.removeLast()

            tenants.lastOrNull()?.let {
                MatchedCursor(
                    when (filterId) {
                        null -> null
                        it.id -> 0
                        else -> 1
                    },
                    it.createdAt,
                    it.id
                )
            }
        } else null

        return SearchRealmTenantResponse(
            tenants,
            requireNotNull(realm.id) { "Realm id is required" },
            nextCursor?.toCursorString(objectMapper)
        )
    }

    @PostMapping("/count")
    fun countRealmSearch(@RequestBody searchRealmTenantRequest: SearchRealmTenantRequest): Long {
        val realm = realmService.findById(searchRealmTenantRequest.realmId)

        return tenantService.countByRealm(realm, searchRealmTenantRequest.query)
    }
}