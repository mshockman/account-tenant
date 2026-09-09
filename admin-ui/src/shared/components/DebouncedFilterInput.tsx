import {useEffect, useState} from "react";
import {TextField} from "@mui/material";


export interface DebouncedFilterInputProps {
    onChanged: (value: string) => void;
    initial?: string;
}


export function DebouncedFilterInput(
    { onChanged, initial = "" }: DebouncedFilterInputProps
) {
    const [filter, setFilter] = useState<string>(initial)
    const [current, setCurrent] = useState<string>(initial)

    useEffect(() => {
        const id = setTimeout(() => {
            if(current != filter) {
                setCurrent(filter);
                onChanged(filter);
            }
        }, 300)

        return () => clearTimeout(id);
    }, [filter])

    return (
        <TextField
            label="Filter"
            size="small"
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            sx={{ width: 300 }}
        />
    )
}