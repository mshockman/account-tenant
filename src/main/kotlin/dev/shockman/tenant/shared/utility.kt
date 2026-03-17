package dev.shockman.tenant.shared

inline fun <T, V> T.applyIfNotNull(value: V?, block: (T, V) -> T): T {
    return value?.let { block(this, it) } ?: this
}