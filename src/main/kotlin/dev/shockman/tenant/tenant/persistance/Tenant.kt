package dev.shockman.tenant.tenant.persistance

import dev.shockman.tenant.realm.persistance.Realm
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tenant")
data class Tenant(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 50)
    val slug: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id", nullable = false)
    val realm: Realm,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Version
    var version: Long = 0,
)