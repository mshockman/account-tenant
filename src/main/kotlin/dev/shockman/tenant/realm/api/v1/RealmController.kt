package dev.shockman.tenant.realm.api.v1

import dev.shockman.tenant.realm.application.RealmService
import dev.shockman.tenant.tenant.application.TenantService
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
import java.util.UUID

@RestController
@RequestMapping("/api/v1/realms")
class RealmController(val realmService: RealmService) {
    @GetMapping("/{id}")
    fun getRealm(
        @PathVariable id: UUID
    ): RealmResponse {
        return realmService.findById(id).toResponse()
    }

    @PostMapping("")
    fun createNewRealm(
        @RequestBody realm: CreateRealmRequest
    ): RealmResponse {
        return realmService.create(realm).toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRealm(
        @PathVariable id: UUID
    ) {
        realmService.findById(id).let {
            realmService.delete(it)
        }
    }

    @GetMapping("/all")
    fun searchRealms(): List<RealmResponse> {
        return realmService.findAll().map { it.toResponse() }
    }

    @PatchMapping("/{id}")
    fun patchRealm(
        @PathVariable id: UUID,
        @RequestBody updateRequest: UpdateRealmRequest
    ): RealmResponse {
        val realm = realmService.findById(id)

        realm.name = updateRequest.name

        return realmService.updateRealm(realm).toResponse()
    }
}