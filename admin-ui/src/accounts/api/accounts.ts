import {apiFetch} from "../../shared/http.ts";
import type {TenantResponse} from "../../tenants/api/TenantResponse.ts";


export interface AccountSearchRequest {
    query: string | null;
    limit: number;
    cursor?: string | null;
    realmId?: string | null;
    tenantId?: string | null;
}


export function searchAccount(request: AccountSearchRequest) {
    return apiFetch<TenantResponse>(
        `/api/v1/accounts/search`,
        {
            method: 'POST',
            body: JSON.stringify(request)
        }
    )
}