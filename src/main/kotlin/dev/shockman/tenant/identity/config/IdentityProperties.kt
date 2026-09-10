package dev.shockman.tenant.identity.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tenant.identity")
data class IdentityProperties(
    val authUrl: String,
    val issuer: String,
    val tokenEndpoint: String,
    val clientId: String,
    val clientSecret: String
)