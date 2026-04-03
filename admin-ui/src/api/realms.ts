import {apiFetch} from "./http.ts";


export interface Realm {
    id: string;
    slug: string;
    name: string;
    createdAt: string;
    updatedAt: string;
}


export interface TenantResponse {
    id: string;
    slug: string;
    name: string;
    createdAt: string;
    updatedAt: string;
    enabled: boolean;
    realmId: string;
}


export interface SearchRealmTenantResponse {
    realmId: string;
    cursor: string | null;
    tenants: TenantResponse[];
}


export interface SearchRealmTenantsRequest {
    realmId: string;

    query: string | null;
    limit: number;

    cursor?: string | null;
}


export function getRealms(): Promise<Realm[]> {
    return apiFetch<Realm[]>(
        `/api/v1/realms/all`
    )
}

export type CreateRealmRequest = {
    name: string;
    slug: string;
}

export function createRealm(request: CreateRealmRequest) {
    return apiFetch<Realm>(
        `/api/v1/realms/create`,
        {
            method: 'POST',
            body: JSON.stringify(request),
        }
    )
}

export function getRealm(id: string) {
    return apiFetch<Realm>(
        `/api/v1/realms/ids/${id}`
    )
}

export function searchRealmTenants(request: SearchRealmTenantsRequest): Promise<SearchRealmTenantResponse> {
    return apiFetch(
        "/api/v1/tenants/search",
        {
            method: 'POST',
            body: JSON.stringify(request),
        }
    )
}

export interface CreateRealmTenantRequest {
    realmId: string;
    name: string;
    slug: string;
    enabled: boolean;
}


export interface RealmTenantResponse {
    id: string;
    slug: string;
    name: string;
    createdAt: string;
    updatedAt: string;
    enabled: boolean;
    realmId: string;
}


export function createRealmTenant(request: CreateRealmTenantRequest): Promise<RealmTenantResponse>  {
    return apiFetch(
        "/api/v1/tenants/create",
        {
            method: 'POST',
            body: JSON.stringify(request),
        }
    )
}

export function searchRealmTenantsCount(request: SearchRealmTenantsRequest): Promise<number> {
    return apiFetch(
        "/api/v1/tenants/count",
        {
            method: 'POST',
            body: JSON.stringify(request),
        }
    )
}


export interface UpdateRealmRequest {
    name: string;
    slug: string;
    id: string;
}


export function updateRealm(request: UpdateRealmRequest): Promise<Realm> {
    const {id, ...rest} = request;

    return apiFetch<Realm>(
        `/api/v1/realms/ids/${id}`,
        {
            method: 'PATCH',
            body: JSON.stringify(rest),
        }
    )
}