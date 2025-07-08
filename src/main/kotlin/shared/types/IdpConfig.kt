package dev.shockman.shared.types

interface IdpConfig

data class OidcIdpConfig (
    val authUrl: String?,
    val tokenUrl: String?,
    val jwksUrl: String?,
    val clientId: String?,
    val clientSecret: String?
) : IdpConfig