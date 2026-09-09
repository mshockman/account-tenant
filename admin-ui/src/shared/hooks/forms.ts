import {useState} from "react";


interface IValidator {
    test: (value: any) => boolean;
}


export class ValidatorError extends Error {
    constructor(message: string) {
        super(message);
    }
}


export class RegExValidator implements IValidator {
    private readonly message: (validator: RegExValidator, value: any) => string;

    constructor(public regex: RegExp, message?: (validator: RegExValidator, value: any) => string) {
        this.message = message ?? (() => 'Value must match pattern ' + regex.toString() + ".");
    }

    test(value: any): boolean {
        if(typeof value !== 'string') {
            throw new Error('Value must be a string');
        }

        if(this.regex.test(value)) {
            return true;
        } else {
            throw new ValidatorError(this.message(this, value));
        }
    }
}

export interface LengthValidatorConfig {
    min?: number;
    max?: number;
    message?: (validator: LengthValidator) => string;
}

export class LengthValidator implements IValidator {
    private readonly min: number | null;
    private readonly max: number | null;
    private readonly message: (validator: LengthValidator, value: any) => string;

    constructor(config: LengthValidatorConfig) {
        if(config.min === undefined && config.max === undefined) {
            throw new Error('At least one of min or max must be specified');
        } else if(config.min !== undefined && config.max !== undefined && config.min > config.max) {
            throw new Error('min must be less than or equal to max');
        }

        this.min = config.min ?? null;
        this.max = config.max ?? null;

        if(config.message) {
            this.message = config.message;
        } else {
            this.message = (validator: LengthValidator, value: any) => {
                const itemName = typeof value === "string" ? 'characters long' : 'items long';

                if(validator.min !== null && validator.max !== null) {
                    return `Value must be between ${validator.min} and ${validator.max} ${itemName}.`;
                } else if(validator.min !== null) {
                    return `Value must be at least ${validator.min} ${itemName}.`;
                } else {
                    return `Value must be at most ${validator.max} ${itemName}.`;
                }
            }
        }
    }

    test(value: any): boolean {
        if(typeof value !== "string" && !("length" in value)) {
            throw new Error('Value must have a length property');
        }

        if(this.min !== null && value.length < this.min) {
            throw new ValidatorError(this.message(this, value));
        } else if(this.max !== null && value.length > this.max) {
            throw new ValidatorError(this.message(this, value));
        } else {
            return true;
        }
    }
}

interface FieldConfig {
    required?: boolean;
    nullable?: boolean;
    validators?: IValidator[];
    initial: any;
    onEmpty?: () => any;
}


interface FormFieldsConfig {
    [key: string]: FieldConfig;
}


interface FormInputProps {
    required?: boolean;
    helperText?: string;
    error?: boolean;
    onChange: (e: any) => void;
    value?: any;
}


interface FormConfig {
    fields: FormFieldsConfig;
    onSubmit: (form: Form) => Promise<void>;
}


interface FormElementProps {
    onSubmit: (e: any) => void;
}


export function NULL() {
    return null;
}


export class Form {
    private readonly config: FormFieldsConfig;
    private errors: {[key: string]: string};
    private values: {[key: string]: any} = {};
    private setters: {[key: string]: (value: any) => void} = {};
    private readonly setErrors: (errors: {[key: string]: string}) => void;
    private validationErrors: {[key: string]: string} = {};
    private readonly onSubmit: (form: Form) => Promise<void>;
    private isSubmitting: boolean;
    private isSubmittingSetter: (isSubmitting: boolean) => void;

    constructor(config: FormConfig) {
        this.config = config.fields;
        const [currentErrors, setErrors] = useState({})
        this.errors = currentErrors;
        this.setErrors = setErrors;
        this.onSubmit = config.onSubmit;
        const [isSubmitting, setIsSubmitting] = useState(false);
        this.isSubmitting = isSubmitting;
        this.isSubmittingSetter = setIsSubmitting;

        for(const key in this.config) {
            const fieldConfig = this.config[key];
            const initialValue = fieldConfig.initial;
            const [currentValue, setValue] = useState(initialValue);
            this.setters[key] = setValue;
            this.setInternalValue(key, currentValue);
        }
    }

    private setInternalValue(key: string, value: any) {
        this.values[key] = value;
        const config = this.getFieldConfig(key);

        if(config.nullable && value === null) {
            return
        } else if(!config.required && value === undefined) {
            return
        } else if(value === "") {
            return
        } else if(config.validators) {
            for(const validator of config.validators) {
                try {
                    validator.test(value);
                } catch (e) {
                    if(e instanceof ValidatorError) {
                        this.validationErrors[key] = e.message;
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    get(key: string): any {
        if(!(key in this.config)) {
            throw new Error(`Field ${key} does not exist in form`);
        }

        return this.values[key];
    }

    set(key: string, value: any): void {
        if(!(key in this.config)) {
            throw new Error(`Field ${key} does not exist in form`);
        }

        this.setters[key](value);
        this.setInternalValue(key, value);
    }

    setInitial(key: string, value: any): void {
        if(!(key in this.config)) {
            throw new Error(`Field ${key} does not exist in form`);
        }

        this.config[key].initial = value;
    }

    reset() {
        this.setErrors({});
        this.errors = {};
        this.validationErrors = {};

        for(const key in this.config) {
            const fieldConfig = this.config[key];
            this.set(key, fieldConfig.initial);
        }
    }

    getFieldConfig(key: string): FieldConfig {
        if(!(key in this.config)) {
            throw new Error(`Field ${key} does not exist in form`);
        }

        return this.config[key];
    }

    getError(key: string): string | null {
        if(!(key in this.config)) {
            throw new Error(`Field ${key} does not exist in form`);
        }

        return this.validationErrors[key] ?? this.errors[key] ?? null;
    }

    field(key: string): FormInputProps {
        const config = this.getFieldConfig(key);

        const r: FormInputProps = {
            onChange: (e: any) => {
                if(config.onEmpty && e.target.value === "") {
                    this.set(key, config.onEmpty());
                } else {
                    this.set(key, e.target.value);
                }
            },
            value: this.get(key),
        }

        if(typeof config.required === "boolean") {
            r.required = config.required;
        }

        const error = this.getError(key);

        if(error !== null) {
            r.error = true;
            r.helperText = error;
        }

        return r
    }

    canSubmit(): boolean {
        if(this.hasErrors()) {
            return false;
        }

        for(const key in this.config) {
            const config = this.getFieldConfig(key);
            if(config.required && (this.get(key) === undefined || this.get(key) === null || this.get(key) === "")) {
                return false;
            }
        }

        return !this.isSaving();
    }

    hasChanged(): boolean {
        for(const key in this.config) {
            if(this.get(key) !== this.getFieldConfig(key).initial) {
                return true;
            }
        }

        return false;
    }

    hasErrors(): boolean {
        return Object.keys(this.errors).length > 0 || Object.keys(this.validationErrors).length > 0;
    }

    form(): FormElementProps {
        return {
            onSubmit: async (e: any) => {
                e.preventDefault();

                if(!this.canSubmit()) {
                    return
                }

                this.isSubmittingSetter(true);
                await this.onSubmit(this);
                this.isSubmittingSetter(false);
                // this.reset();
            }
        }
    }

    isSaving(): boolean {
        return this.isSubmitting;
    }

    validate(): Record<string, any> {
        if(this.hasErrors()) {
            throw new Error("Form has errors");
        }

        return this.values;
    }
}


export function useForm(config: FormConfig): Form {
    return new Form(config);
}