package dev.shockman.controller

import dev.shockman.dto.CreateIdentityProvider
import dev.shockman.dto.IdentityProviderResponse
import dev.shockman.dto.RealmIdentityProviderResponse
import dev.shockman.dto.UpdateRealmIdentityProvider
import dev.shockman.dto.toIdentityProviderResponse
import dev.shockman.dto.toRealmIdentityProviderResponse
import dev.shockman.service.IdentityProviderService
import dev.shockman.service.RealmService
import dev.shockman.service.TenantService
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
@RequestMapping("/realms/{realmSlug}/")
class RealmIdentityProviderController(
    val realmService: RealmService,
    val identityProviderService: IdentityProviderService
) {
    @PostMapping("identity-providers")
    fun createRealmIdentityProvider(
        @PathVariable("realmSlug") realmSlug: String,
        @RequestBody createIdentityProviderRequest: CreateIdentityProvider
    ): RealmIdentityProviderResponse {
        val realm = realmService.findBySlug(realmSlug)
        return identityProviderService.createRealmIdentityProvider(realm, createIdentityProviderRequest).toRealmIdentityProviderResponse()
    }

    @GetMapping("identity-providers/{idpId}")
    fun getIdentityProviderById(
        @PathVariable("realmSlug") realmSlug: String,
        @PathVariable("idpId") id: UUID
    ): IdentityProviderResponse {
        return identityProviderService.getRealmIdentityProviderById(
            realmService.findBySlug(realmSlug),
            id
        ).toIdentityProviderResponse()
    }

    @PatchMapping("identity-providers/{idpId}")
    fun updateIdentityProviderById(
        @PathVariable("realmSlug") realmSlug: String,
        @PathVariable("idpId") id: UUID,
        @RequestBody realmIdentityProviderUpdate: UpdateRealmIdentityProvider
    ): RealmIdentityProviderResponse {
        val realm = realmService.findBySlug(realmSlug)
        val idp = identityProviderService.getRealmScopeIdentityProviderById(realm, id)

        return identityProviderService.updateIdentityProvider(
            idp,
            realmIdentityProviderUpdate
        ).toRealmIdentityProviderResponse()
    }

    @DeleteMapping("identity-providers/{idpId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteIdentityProvider(
        @PathVariable("realmSlug") realmSlug: String,
        @PathVariable("idpId") id: UUID
    ) {
        val realm = realmService.findBySlug(realmSlug)
        val idp = identityProviderService.getRealmScopeIdentityProviderById(realm, id)

        return identityProviderService.delete(idp)
    }
}