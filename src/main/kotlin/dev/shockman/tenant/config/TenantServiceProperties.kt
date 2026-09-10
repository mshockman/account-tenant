package dev.shockman.tenant.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "tenant")
data class TenantServiceProperties(
    val baseUrl: URI,
)