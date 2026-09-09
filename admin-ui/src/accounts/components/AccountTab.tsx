import {useSearchAccounts} from "../api/accounts.ts";
import {Box, Button} from "@mui/material";
import * as React from "react";
import {type DataTableFetchFunction, FilterDataTable} from "../../shared/components/FilteredDataTable.tsx";
import {CreateAccountDialog} from "./CreateAccountDialog.tsx";


export interface AccountTabProps {
    realmId?: string | null;
    tenantId?: string | null;
}


export function AccountTab(
    { realmId = null, tenantId = null }: AccountTabProps = { realmId: null, tenantId: null }
) {
    const [open, setOpen] = React.useState(false);

    const fetchAccounts: DataTableFetchFunction = (query: string | null = null, cursor: string | null = null) => {
        const r = useSearchAccounts({
            realmId: realmId,
            tenantId: tenantId,
            query: query,
            limit: 10,
            cursor: cursor,
        })

        if(r.isLoading) {
            return {
                count: 0,
                cursor: null,
                data: [],
                isLoading: true,
            }
        } else {
            return {
                count: r.data?.count ?? 0,
                cursor: r.data?.cursor ?? null,
                data: r.data?.accounts ?? [],
                isLoading: false,
            }
        }
    }

    return (
        <Box>
            <FilterDataTable
                columns={[
                    {field: "id", label: "ID", primaryKey: true},
                    {field: "username", label: "Username"},
                    {field: "email", label: "Email"},
                    {field: "phone", label: "Phone"},
                    {field: "firstName", label: "First Name"},
                    {field: "lastName", label: "Last Name"},
                ]}
                title={"Accounts"}
                fetchApi={fetchAccounts}
                actions={
                    tenantId ? <Button type="button" onClick={() => setOpen(true)}>Create Account</Button> : undefined
                }
            />
            {tenantId && <CreateAccountDialog setOpen={setOpen} isOpen={open} tenantId={tenantId} />}
        </Box>
    )
}