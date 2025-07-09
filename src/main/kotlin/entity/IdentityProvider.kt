package dev.shockman.entity

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdentityProviderType
import dev.shockman.shared.types.IdpConfig
import dev.shockman.shared.types.OidcIdpConfig
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import org.hibernate.engine.spi.SharedSessionContractImplementor
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "identity_provider")
data class IdentityProvider(
    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    val id: UUID = UUID.randomUUID(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id", nullable = false)
    val realm: Realm,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = true)
    val tenant: Tenant?,

    @Column(nullable = true, length = 320)
    var issuer: String,

    @Column(nullable = true, length = 100)
    var name: String?,

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    @Column(nullable = true)
    val type: IdentityProviderType?,

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    @Column(nullable = true, name="default_sync_mode")
    var defaultSyncMode: IdentityProviderMapperSyncMode?,

    @Column(columnDefinition = "jsonb")
    @Type(IdpConfigType::class)
    var config: IdpConfig
)

class IdpConfigType : JsonBinaryType() {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun nullSafeGet(
        rs: ResultSet,
        position: Int,
        session: SharedSessionContractImplementor?,
        owner: Any?
    ): Any? {
        val value = rs.getString(position) ?: return null

        try {
            val type = IdentityProviderType.valueOf(rs.getString(rs.findColumn("type")))

            return when(type) {
                IdentityProviderType.OIDC -> objectMapper.readValue(value, OidcIdpConfig::class.java)
            }
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Failed to parse IdpConfig JSON: $value", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun returnedClass(): Class<Any> = IdpConfig::class.java as Class<Any>
}