import {useQuery} from "@tanstack/react-query";
import {getTenant} from "../api/tenants.ts";


export function useTenant(id: string) {
    return useQuery({
        queryKey: ['tenant', id],
        queryFn: () => getTenant(id),
    })
}