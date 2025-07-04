# Tenant Service

The Tenant Service is a stateless microservice for managing tenants and their associated accounts. Its primary responsibilities are:

- Managing realms, tenants, and accounts
- Providing RESTful APIs for CRUD operations
- Managing identity linking flows
- Handling communication (emails and SMS)
- Providing identity verification through a "whoami" endpoint

## Tech Stack
- Kotlin
- Spring
- PostgreSQL
- Docker

## Core Concepts

### Realm
A top-level organizational unit that groups multiple tenants together. Realms are isolated ecosystems - tenants in different realms operate independently.

### Tenant
An organization or company within a realm that can have multiple user accounts.

### Account
A user entity that belongs to a specific tenant. One identity can be associated with multiple accounts.

### Identity
A user's authentication method through an identity provider. Represents the actual login credentials and method.

# Endpoints

## Realm

### POST /realms

Creates a new realm.

### GET /realms/{realmSlug}

Gets the realm entity by it's slug.

### PATCH /realms/{realmSlug}

Updates the realm entity.

### DELETE /realms/{realmSlug}

Deletes the specified realm.

## Tenants

### POST /realms/{realmSlug}/tenants

Create a new tenant.

### GET /realms/{realmSlug}/tenants/{tenantSlug}

Gets the tenant.

### PATCH /realms/{realmSlug}/tenants/{tenantSlug}

Updates the tenant

### DELETE /realms/{realmSlug}/tenants/tenantSlug}

Delete the tenant.

## Accounts

### POST /realms/{realmSlug}/tenants/{tenantSlug}/accounts

Create a new account.

### GET /realms/{realmSlug}/tenants/{tenantSlug}/accounts/{accountId}

Finds an account by it's account id.

### PATCH /realms/{realmSlug}/tenants/{tenantSlug}/accounts/{accountId}

Updates an account by it's account id.

### DELETE /realms/{realmSlug}/tenants/{tenantSlug}/accounts/{accountId}

Delete an account by it's account id.

### GET /realms/{realmSlug}/tenants/{tenantSlug}/accounts

Gets a paginated list of all accounts ordered by createdAt, id.  A nextCursor is provided to get the next batch of accounts. This cursor can be saved to retrieve new accounts at a later date.

### GET /realms/{realmSlug}/tenants/{tenantSlug}/updated-accounts

Gets a paginated list of all accounts ordered by updatedAt, id.  A nextCursor is provided to get the next batch of accounts.  You can save this cursor once you reach the end to get any future updates at a later date.

### POST /realms/{realmSlug}/tenants/{tenantSlug}/search-accounts

Gets a paginated list of accounts that can be filtered.

## Identity Providers

### POST /realms/{realmSlug}/identity-providers

Creates a new realm scoped identity provider that can be used by all tenants that allow them.

### GET /realms/{realmSlug}/identity-providers

Gets a paginated list of identity providers ordered by (`createdAt`, `id`).

### GET /realms/{realmSlug}/identity-providers/{idpId}

Gets an identity provider by it's id.

### PATCH /realms/{realmSlug}/identity-providers/{idpId}

Updates a identity provider by it's idp id.

### DELETE /realms/{realmSlug}/identity-providers/{idpId}

Deletes an identity provider by it's idp id.