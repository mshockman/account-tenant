import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {createRealm, getRealms} from "../api/realms.ts";

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