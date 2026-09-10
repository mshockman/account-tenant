package dev.shockman.tenant.identity.application

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("id_token")
    val idToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,
)