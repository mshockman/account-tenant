package dev.shockman.tenant.accounts.persistance

import dev.shockman.tenant.identity.persistance.AccountIdentity
import dev.shockman.tenant.tenant.persistance.Tenant
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "account")
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant? = null,

    @Version
    var version: Long = 0,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = true, name = "first_name", length = 100)
    var firstName: String? = null,

    @Column(nullable = true, name = "last_name", length = 100)
    var lastName: String? = null,

    @Column(nullable = true, length = 100)
    var username: String,

    @Column(nullable = true, length = 255, name="remote_id")
    var remoteId: String? = null,

    @Column(nullable = true, length = 320)
    var email: String? = null,

    @Column(nullable = true, length = 20)
    var phone: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    val tenant: Tenant,

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType::class)
    var attributes: Map<String, Any> = emptyMap(),

    @OneToMany(mappedBy = "account")
    var identities: MutableSet<AccountIdentity> = mutableSetOf()
)