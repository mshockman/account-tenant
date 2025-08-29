package dev.shockman.service

import com.authzed.api.v1.ObjectReference
import com.authzed.api.v1.PermissionsServiceGrpc
import com.authzed.api.v1.Relationship
import com.authzed.api.v1.RelationshipUpdate
import com.authzed.api.v1.SubjectReference
import com.authzed.api.v1.WriteRelationshipsRequest
import dev.shockman.entity.Account
import dev.shockman.entity.Tenant
import org.springframework.stereotype.Service

@Service
class AuthorizationService(private val permissionGrpcService: PermissionsServiceGrpc.PermissionsServiceBlockingStub) {
    companion object {
        const val ACCOUNT_OBJECT = "account"
        const val TENANT_OBJECT = "tenant"

        const val ACCOUNT_TENANT_RELATIONSHIP = "tenant"
    }

    fun linkAccountToTenant(account: Account, tenant: Tenant) {
        val relationship = Relationship.newBuilder().setResource(
            ObjectReference.newBuilder().setObjectType(ACCOUNT_OBJECT).setObjectId(account.id.toString())
        ).setRelation(ACCOUNT_TENANT_RELATIONSHIP).setSubject(
            SubjectReference.newBuilder().setObject(
                ObjectReference.newBuilder().setObjectType(TENANT_OBJECT).setObjectId(tenant.id.toString())
            )
        )

        val update = RelationshipUpdate.newBuilder().setOperation(RelationshipUpdate.Operation.OPERATION_CREATE).setRelationship(relationship).build()

        permissionGrpcService.writeRelationships(
            WriteRelationshipsRequest.newBuilder().addUpdates(update).build()
        )
    }
}