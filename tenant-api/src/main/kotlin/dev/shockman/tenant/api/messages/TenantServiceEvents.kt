package dev.shockman.tenant.api.messages

const val TENANT_ACCOUNT_EVENT_TOPIC = "tenant.account.events"
const val TENANT_REALM_EVENT_TOPIC = "tenant.realm.events"
const val TENANT_TENANT_EVENT_TOPIC = "tenant.tenant.events"
const val TENANT_IDENTITY_EVENT_TOPIC = "tenant.identity.events"

object TenantServiceEvents {
    const val ACCOUNT_CREATED = "tenant.account.created.v1"
    const val ACCOUNT_UPDATED = "tenant.account.updated.v1"
    const val ACCOUNT_DELETED = "tenant.account.deleted.v1"

    const val ACCOUNT_LINKED = "tenant.account.identity.linked.v1"

    const val TENANT_CREATED = "tenant.tenant.created.v1"
    const val TENANT_UPDATED = "tenant.tenant.updated.v1"
    const val TENANT_DELETED = "tenant.tenant.deleted.v1"

    const val REALM_CREATED = "tenant.realm.created.v1"
    const val REALM_UPDATED = "tenant.realm.updated.v1"
    const val REALM_DELETED = "tenant.realm.deleted.v1"

    const val IDENTITY_CREATED = "tenant.identity.created.v1"
}