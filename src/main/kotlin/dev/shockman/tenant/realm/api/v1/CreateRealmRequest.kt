package dev.shockman.tenant.realm.api.v1

import jakarta.validation.constraints.Size

data class CreateRealmRequest(
    @field:Size(min=1, max=100)
    val name: String
)