# Realm

A realm is the top level organizational unit of the tenant service.  It represents something like a multi tenant application.
Tenants and accounts in different realms should be completely isolated from each other.

# Tenant

Tenants are a unit of organization that can represent something like a company.  A tenant owns most resources.
A tenant can have one or more accounts that can access it's resources.  A tenant must have at least one account that acts
as the account owner and admin for the tenant.

# Accounts

Accounts are users of a tenant.  All tenants must have at least one admin account that is used to configure the tenant.

Other than the admin account a tenant can have multiple other accounts that can access it's resources.  Accounts are the users
for the tenant.  They can represent something like employees for a company.  Accounts can be assigned specific permissions
and roles to limit the resources and actions that they have access too.

# Creating a new Realm

To create a new realm a post request must be made to the `/realms` endpoint, providing the name and slug for the realm.

Name is a human readable identifier for the realm.  Name doesn't have to be unique for every realm but it is best practice to do so.

Slug is a natural unique identifier for the realm.  It is used in the endpoints for managing realm resources.  All realm slugs must be unique
and match the expression `^[a-zA-Z][a-zA-Z0-9\-_]*$` and can be no mor than 30 characters in length.