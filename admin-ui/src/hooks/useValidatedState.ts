import {useState} from "react";

export type TestValidatorFunction<T> = (value: T) => boolean;


export interface IValidator<T> {
    test: TestValidatorFunction<T>;
    message: string;
}


export class Validator<T> implements IValidator<T> {
    readonly test: (value: T) => boolean;
    readonly message: string;

    constructor(
        message: string,
        test: TestValidatorFunction<T>,
    ) {
        this.message = message;
        this.test = test;
    }
}


export class RegExpValidator implements IValidator<string> {
    readonly message: string;
    private readonly regex: RegExp;

    constructor(message: string, regex: RegExp) {
        this.message = message;
        this.regex = regex;
    }

    test(value: string): boolean {
        value = value.trim();

        if(value) {
            return this.regex.test(value);
        } else {
            return true;
        }
    }
}


export function useValidatedState<S>(initial: S, validators: IValidator<S>[]): [S, (value: S) => void, string | null] {
    const [state, setState] = useState<S>(initial)

    for(const validator of validators) {
        if(!validator.test(state)) {
            return [state, setState, validator.message]
        }
    }

    return [state, setState, null]
}