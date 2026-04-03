package dev.shockman.tenant.realm.api.v1

import jakarta.validation.constraints.Size

data class UpdateRealmRequest(
    @field:Size(min=1, max=100)
    val name: String,

    @field:Size(min=1, max=50)
    val slug: String
)