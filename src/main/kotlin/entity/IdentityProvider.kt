package dev.shockman.entity

import dev.shockman.shared.types.IdentityProviderMapperSyncMode
import dev.shockman.shared.types.IdentityProviderType
import dev.shockman.shared.types.IdpConfig
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
    @Type(JsonBinaryType::class)
    var config: IdpConfig
)