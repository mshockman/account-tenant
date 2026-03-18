package dev.shockman.tenant.realm.api.v1

import dev.shockman.tenant.realm.application.RealmService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/realms/by-slug/{slug}")
class RealmSlugController(
    val realmService: RealmService
) {
    @GetMapping
    fun getRealmBySlug(
        @PathVariable slug: String
    ): RealmResponse {
        val realm = realmService.findBySlug(slug)
        return realm.toResponse()
    }
}