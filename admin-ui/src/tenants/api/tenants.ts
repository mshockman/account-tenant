import type {TenantResponse} from "./TenantResponse.ts";
import {apiFetch} from "../../shared/http.ts";

export function getTenant(id: string) {
    return apiFetch<TenantResponse>(
        `/api/v1/tenants/${id}`,
        {
            method: 'GET',
        }
    )
}