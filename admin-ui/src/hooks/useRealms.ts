import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {
    createRealm,
    createRealmTenant,
    getRealm,
    getRealms,
    searchRealmTenants, searchRealmTenantsCount,
    type SearchRealmTenantsRequest, updateRealm
} from "../api/realms.ts";

export function useRealms() {
    return useQuery({
        queryKey: ['realms'],
        queryFn: () => getRealms(),
    })
}


export function useCreateRealm() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createRealm,
        onSuccess: () => {
            return queryClient.invalidateQueries({queryKey: ['realms']})
        },
    })
}

export function useGetRealm(id: string | null | undefined) {
    return useQuery({
        queryKey: ['realm', id],
        queryFn: () => getRealm(id!),
        enabled: !!id
    })
}

export function useSearchRealmTenants(request: SearchRealmTenantsRequest) {
    return useQuery({
        queryKey: ['search-realm-tenants', request.realmId, request.query || '', request.limit, request.cursor || ''],
        queryFn: () => searchRealmTenants(request),
    })
}

export function useSearchRealmTenantsCount(request: SearchRealmTenantsRequest) {
    return useQuery({
        queryKey: ['search-realm-tenants', request.realmId, "count", request.query || ''],
        queryFn: () => searchRealmTenantsCount(request),
    })
}

export function useCreateRealmTenant() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: createRealmTenant,
        onSuccess: (response) => {
            return queryClient.invalidateQueries({queryKey: ['search-realm-tenants', response.realmId]})
        },
    })
}


export function useUpdateRealm() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: updateRealm,
        onSuccess: (response) => {
            queryClient.setQueryData(['realm', response.id], response)
            return queryClient.invalidateQueries({queryKey: ['realms']})
        }
    })
}