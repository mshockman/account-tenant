

export function slotProps(maxLength: number, required: boolean=true) {
    return {
        slotProps: {
            htmlInput: {
                required: required,
                maxLength: maxLength,
            }
        }
    }
}