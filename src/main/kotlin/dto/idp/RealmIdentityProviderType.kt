package dev.shockman.dto.idp

import dev.shockman.shared.types.IdentityProviderType

enum class RealmIdentityProviderType {
    OIDC,
}

fun RealmIdentityProviderType.toIdentityProviderType(): IdentityProviderType {
    return when(this) {
        RealmIdentityProviderType.OIDC -> IdentityProviderType.OIDC
    }
}