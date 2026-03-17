import {apiFetch} from "./http.ts";


export interface Realm {
    id: string;
    slug: string;
    name: string;
    createdAt: string;
    updatedAt: string;
}


export function getRealms(): Promise<Realm[]> {
    return apiFetch<Realm[]>(
        `/api/realms/`
    )
}

export type CreateRealmRequest = {
    name: string;
    slug: string;
}

export function createRealm(request: CreateRealmRequest) {
    return apiFetch<Realm>(
        `/api/realms/create`,
        {
            method: 'POST',
            body: JSON.stringify(request),
        }
    )
}

export function getRealm(id: string) {
    return apiFetch<Realm>(
        `/api/realms/${id}`
    )
}