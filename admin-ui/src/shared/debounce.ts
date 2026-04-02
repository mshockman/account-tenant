
export function debounce(func: Function, wait: number) : Function {
    let timeout: number | null = null;

    return (...args: any[]) => {
        if(timeout != null) {
            clearTimeout(timeout);
            timeout = null;
        }

        timeout = setTimeout(() => {
            timeout = null;
            func(...args);
        }, wait)
    }
}