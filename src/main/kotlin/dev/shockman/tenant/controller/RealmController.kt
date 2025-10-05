package dev.shockman.tenant.controller

import dev.shockman.tenant.dto.CreateRealmRequest
import dev.shockman.tenant.dto.RealmResponse
import dev.shockman.tenant.dto.toResponse
import dev.shockman.tenant.service.RealmService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/realms")
class RealmController(val realmService: RealmService) {
    @GetMapping
    fun getRealm(@RequestParam("id") id: UUID): RealmResponse {
        return realmService.findById(id).toResponse()
    }

    @GetMapping("/{slug}")
    fun getRealmBySlug(@PathVariable("slug") slug: String): RealmResponse {
        return realmService.findBySlug(slug).toResponse()
    }

    @PostMapping
    fun createNewRealm(@RequestBody realm: CreateRealmRequest): RealmResponse {
        return realmService.create(realm).toResponse()
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRealm(@RequestParam("id") id: UUID) {
        val realm = realmService.findById(id)
        realmService.delete(realm)
    }
}