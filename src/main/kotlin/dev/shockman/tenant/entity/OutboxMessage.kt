package dev.shockman.tenant.entity

import com.fasterxml.jackson.databind.JsonNode
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

enum class OutboxMessageStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    BACKOFF
}

@Entity
@Table(name = "outbox")
data class OutboxMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // matches autoIncrement/identity
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(columnDefinition = "uuid")
    val eventId: UUID = UUID.randomUUID(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @Column(name = "aggregate_id", nullable = false)
    var aggregateId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OutboxMessageStatus = OutboxMessageStatus.PENDING,

    @Column(nullable = false)
    val topic: String,

    @Column(nullable = false)
    val messageType: String,

    @Column(columnDefinition = "jsonb")
    @Type(JsonBinaryType::class)
    val message: JsonNode,

    @Column(nullable = false)
    var attempt: Int = 0,

    @Column(name = "claimed_at", nullable = true)
    var claimedAt: Instant? = null,

    @Column(name = "claimed_by", nullable = true)
    var claimedBy: String? = null,

    @Column(name = "claim_expires_at", nullable = true)
    var claimExpiresAt: Instant? = null,

    @Column(name = "processed_at", nullable = true)
    var processedAt: Instant? = null,

    @Column(name = "available_at", nullable = false)
    var availableAt: Instant = Instant.now(),

    @Column(nullable = true)
    var error: String? = null,

    @Column(nullable = true, name = "correlation_id")
    val correlationId: String? = null,
)