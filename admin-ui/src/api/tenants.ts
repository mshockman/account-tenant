import {apiFetch} from "./http.ts";
import type {TenantResponse} from "./TenantResponse.ts";

export function getTenant(id: string) {
    return apiFetch<TenantResponse>(
        `/api/v1/tenants/ids/${id}`,
        {
            method: 'GET',
        }
    )
}