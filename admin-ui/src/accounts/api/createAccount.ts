import {useMutation, useQueryClient} from "@tanstack/react-query";
import {apiFetch} from "../../shared/http.ts";
import type {JsonValue} from "../../shared/types/JsonValue.ts";
import type Account from "../persistance/Account.ts";


export function useCreateAccount() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createAccount,
        onSuccess: async () => {
            await queryClient.invalidateQueries({queryKey: ['accounts', 'search']})
        }
    })
}


export interface CreateAccountRequest {
    tenantId: string;
    username: string;
    firstName?: string | null;
    lastName?: string | null;
    phone?: string | null;
    email?: string | null;
    enabled?: boolean;
    attributes?: Record<string, JsonValue>;
}


export function createAccount(request: CreateAccountRequest) {
    return apiFetch<Account>(
        `/api/v1/accounts`,
        {
            method: 'POST',
            body: JSON.stringify(request),
        }
    )
}