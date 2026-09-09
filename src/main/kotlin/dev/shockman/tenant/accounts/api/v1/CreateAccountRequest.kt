package dev.shockman.tenant.accounts.api.v1

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateAccountRequest(
    val tenantId: UUID,

    @field:Size(min=1, max=255)
    @field:NotBlank
    val username: String,

    @field:Size(min=1, max=255)
    val firstName: String? = null,

    @field:Size(min=1, max=255)
    val lastName: String? = null,

    @field:Email
    val email: String? = null,

    @field:Pattern(regexp = "^\\+?[0-9]\\d{1,14}$", message = "Phone number must be a valid E.164-style number")
    val phone: String? = null,

    val enabled: Boolean = true,
    val attributes: Map<String, Any> = emptyMap()
)