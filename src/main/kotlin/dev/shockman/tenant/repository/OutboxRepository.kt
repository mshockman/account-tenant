package dev.shockman.tenant.repository

import dev.shockman.tenant.entity.OutboxMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface OutboxRepository : JpaRepository<OutboxMessage, Long> {
    @Modifying
    @Query(value = """
    with candidates as (
        SELECT
            o.id,
            row_number() over (partition by o.aggregate_id order by o.created_at, o.id) as rn
        FROM outbox o
        WHERE
            o.status IN ('PENDING', 'PROCESSING', 'FAILED', 'BACKOFF')
    ),
    claimed as (
        SELECT
            o.id
        FROM
            outbox o JOIN candidates c ON o.id = c.id AND c.rn = 1
        WHERE
            (o.status IN ('PENDING', 'BACKOFF') AND o.available_at <= now())
            OR (o.status = 'PROCESSING' AND (o.claim_expires_at <= now() OR o.claim_expires_at IS NULL))
        ORDER BY o.created_at, o.id
        LIMIT :limit
        FOR UPDATE OF o SKIP LOCKED
    )
    UPDATE
        outbox o
    SET
        status = 'PROCESSING',
        claimed_at = now(),
        claim_expires_at = now() + make_interval(secs => :leaseSeconds),
        claimed_by = :claimedBy
    FROM claimed c
    WHERE
        o.id = c.id
    returning o.id
    """, nativeQuery = true)
    fun claimRows(claimedBy: String, leaseSeconds: Long = 60, limit: Int = 10): List<Long>

    @Modifying
    @Query("""
    UPDATE 
        outbox o
    SET 
        claim_expires_at = now() + make_interval(secs => :leaseSeconds)
    WHERE 
        id = :id
        AND status = 'PROCESSING'
        AND claimed_by = :claimedBy
        AND claim_expires_at > now()
    """, nativeQuery = true)
    fun renewLease(id: Long, claimedBy: String, leaseSeconds: Long): Int

    @Modifying
    @Query("""
    UPDATE 
        outbox o
    SET 
        claim_expires_at = :newExpires
    WHERE 
        id = :id
        AND status = 'PROCESSING'
        AND claimed_by = :claimedBy
        AND claim_expires_at > now()
        AND :newExpires > now()
        AND :newExpires > claim_expires_at
    """, nativeQuery = true)
    fun renewLease(id: Long, claimedBy: String, newExpires: Instant): Int

    @Query("SELECT claim_expires_at FROM outbox WHERE id = :id", nativeQuery = true)
    fun getClaimExpiresAtById(id: Long): Instant?
}