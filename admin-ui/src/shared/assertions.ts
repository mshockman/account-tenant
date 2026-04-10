
export function requireDefined<T>(value: T | undefined | null, message: string = "Value is undefined or null"): T {
    if(value === undefined || value === null) {
        throw new Error(message);
    }

    return value as T;
}