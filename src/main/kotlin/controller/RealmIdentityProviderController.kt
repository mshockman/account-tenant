package dev.shockman.controller

import com.fasterxml.jackson.databind.node.ObjectNode
import dev.shockman.dto.idp.CreateRealmIdentityProvider
import dev.shockman.dto.idp.IdentityProviderResponse
import dev.shockman.dto.idp.toIdentityProviderResponse
import dev.shockman.service.IdentityProviderService
import dev.shockman.service.RealmService
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
        @RequestBody createIdentityProviderRequest: CreateRealmIdentityProvider
    ): IdentityProviderResponse {
        val realm = realmService.findBySlug(realmSlug)
        return identityProviderService.createRealmIdentityProvider(realm, createIdentityProviderRequest).toIdentityProviderResponse()
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
        @RequestBody realmIdentityProviderUpdate: ObjectNode
    ): IdentityProviderResponse {
        val realm = realmService.findBySlug(realmSlug)
        val idp = identityProviderService.getRealmScopeIdentityProviderById(realm, id)

        return identityProviderService.updateIdentityProvider(
            idp,
            realmIdentityProviderUpdate
        ).toIdentityProviderResponse()
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