import {
    Box, Button, Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from "@mui/material";
import {type JSX, useState} from "react";
import {DebouncedFilterInput} from "./DebouncedFilterInput.tsx";


interface DataTableQueryResult {
    count: number;
    cursor: string | null;
    data: any[];
    isLoading: boolean;
}


export type DataTableFetchFunction = (query?: string | null, cursor?: string | null) => DataTableQueryResult


export interface FilterDataTableColumn {
    field: string;
    label: string;
    primaryKey?: boolean;
}


export interface FilterDataTableProps {
    columns: FilterDataTableColumn[];
    fetchApi: DataTableFetchFunction;
    title: string;
    actions?: JSX.Element;
}


export function FilterDataTable(
    {
        columns, fetchApi, title, actions
    }: FilterDataTableProps
) {
    const [query, setQuery] = useState<string>("")
    const [cursors, setCursors] = useState<(string | null)[]>([null]);
    const [cursorIndex, setCursorIndex] = useState<number>(0);

    const cursor = cursors[cursorIndex];

    const dataSearch = fetchApi(query, cursor)
    const nextPageCursor = dataSearch.cursor ?? null;
    const totalItemCount = dataSearch.count ?? 0;

    const onFilterChange = (value: string) => {
        setQuery(value)
        setCursorIndex(0);
        setCursors([null]);
    }

    const previousPage = () => {
        if(cursorIndex > 0) {
            setCursorIndex(cursorIndex - 1);
        }
    }

    const nextPage = () => {
        cursors.push(nextPageCursor);
        setCursors([...cursors]);
        setCursorIndex(cursorIndex + 1);
    }

    return (
        <>
            <Box>
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        marginBottom: 2,
                        gap: 2,
                    }}
                >
                    <Typography variant="h5">{title}</Typography>

                    <Box sx={{ display: "flex", gap: 2 }}>
                        <DebouncedFilterInput onChanged={onFilterChange} initial="" />
                        <Box>
                            {actions}
                        </Box>
                    </Box>
                </Box>
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                {columns.map(column => {
                                    return (
                                        <TableCell key={column.field}>{column.label}</TableCell>
                                    )
                                })}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {dataSearch.isLoading && <TableRow><TableCell colSpan={columns.length}>Loading...</TableCell></TableRow>}
                            {!dataSearch.isLoading && dataSearch.data?.map(item => {
                                const primaryKey = columns.find(column => column.primaryKey === true);

                                if(!primaryKey) {
                                    throw new Error("No primary key found in columns");
                                }

                                return (
                                    <TableRow key={item[primaryKey.field]}>
                                        {columns.map(column => {
                                            return (
                                                <TableCell key={column.field}>{item[column.field]}</TableCell>
                                            )
                                        })}
                                    </TableRow>
                                )
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
                <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 2 }}>
                    <Button disabled={cursorIndex <= 0} onClick={previousPage}>Previous</Button>
                    <Box>{totalItemCount || 0} Items Total</Box>
                    <Button disabled={nextPageCursor === null} onClick={nextPage}>Next</Button>
                </Box>
            </Box>
        </>
    )
}