package dev.shockman.tenant.tenant.application

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.UUID
import java.time.Instant
import java.util.Base64

data class MatchedCursor(
    val rank: Int? = null,
    val after: Instant,
    val id: UUID
)

fun MatchedCursor.toCursorString(objectMapper: ObjectMapper): String {
    val jsonString = objectMapper.writeValueAsString(this)
    return Base64.getEncoder().encodeToString(jsonString.toByteArray(Charsets.UTF_8))
}

fun String.toMatchedCursor(objectMapper: ObjectMapper): MatchedCursor {
    val json = String(Base64.getDecoder().decode(this), Charsets.UTF_8)
    return objectMapper.readValue(json, MatchedCursor::class.java)
}