package dev.shockman.tenant.identity.persistance

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountIdentityLinkRequestRepository : JpaRepository<AccountIdentityLink, UUID> {
}