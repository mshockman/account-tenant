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
    return useQuery({
        queryKey: ['search-accounts', request.realmId ?? null, request.tenantId ?? null, request.query ?? null, request.limit, request.cursor ?? null],
        queryFn: () => searchAccount(request),
    })
}