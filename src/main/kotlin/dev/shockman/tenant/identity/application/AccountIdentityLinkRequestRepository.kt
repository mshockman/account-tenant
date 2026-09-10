package dev.shockman.tenant.identity.application

import dev.shockman.tenant.identity.persistance.AccountIdentityLink
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountIdentityLinkRequestRepository : JpaRepository<AccountIdentityLink, UUID> {
}