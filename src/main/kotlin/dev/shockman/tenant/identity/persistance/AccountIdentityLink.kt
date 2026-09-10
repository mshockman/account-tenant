package dev.shockman.tenant.identity.persistance

import dev.shockman.tenant.accounts.persistance.Account
import dev.shockman.tenant.tenant.persistance.Tenant
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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "account_identity_link_request")
class AccountIdentityLink(
    @Id
    @Column(columnDefinition = "uuid")
    var id: UUID = UUID.randomUUID(),

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    var account: Account,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "request_type", nullable = false)
    var requestType: LinkRequestType,

    @Column(name = "expires", nullable = true)
    var expires: Instant? = null,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    var status: LinkRequestStatus,
) {
}