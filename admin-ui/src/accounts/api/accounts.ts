import {apiFetch} from "../../shared/http.ts";
import {useQuery} from "@tanstack/react-query";
import type Account from "../persistance/Account.ts";


export interface AccountSearchRequest {
    query: string | null;
    limit: number;
    cursor?: string | null;
    realmId?: string | null;
    tenantId?: string | null;
}


export interface AccountSearchResponse {
    cursor: string | null;
    accounts: Account[];
    tenantId: string | null;
    count: number;
}


export function searchAccount(request: AccountSearchRequest) {
    return apiFetch<AccountSearchResponse>(
        `/api/v1/accounts/search`,
        {
            method: 'POST',
            body: JSON.stringify(request)
        }
    )
}


export function useSearchAccounts(request: AccountSearchRequest) {
    const realmId = request.realmId ?? null;
    const tenantId = request.tenantId ?? null;
    const cursor = request.cursor ?? null;
    const limit = request.limit;
    const query = request.query ?? null;
    const scope: { realmId: string | null, tenantId: string | null } = { realmId, tenantId };

    return useQuery({
        queryKey: [
            'accounts',
            'search',
            scope,
            query,
            limit,
            cursor
        ],
        queryFn: () => searchAccount(request),
    })
}