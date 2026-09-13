package dev.shockman.tenant.realm.persistance

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "realm")
class Realm(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @Version
    var version: Long = 0,
)