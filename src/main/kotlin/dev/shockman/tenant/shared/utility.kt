package dev.shockman.tenant.shared

import java.util.UUID

inline fun <T, V> T.applyIfNotNull(value: V?, block: (T, V) -> T): T {
    return value?.let { block(this, it) } ?: this
}

fun String.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (_: IllegalArgumentException) {
        null
    }
}