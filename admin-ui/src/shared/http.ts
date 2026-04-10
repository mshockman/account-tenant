

export async function apiFetch<T>(
    url: RequestInfo | URL,
    init?: RequestInit
): Promise<T> {
    const response = await fetch(url, {
        ...init,
        headers: {
            'Content-Type': 'application/json',
            ...init?.headers,
        },
    });

    if(!response.ok) {
        const text = await response.text();
        throw new Error(text || `HTTP ${response.status}`);
    }

    if(response.status === 204) {
        return undefined as unknown as T;
    }

    return response.json()
}