import {useSearchRealmTenants, useSearchRealmTenantsCount} from "../hooks/useRealms.ts";
import {type ReactNode, useEffect, useState} from "react";
import {
    Alert,
    Box,
    Button, Link,
    Paper, Snackbar, Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography
} from "@mui/material";
import {Link as RouterLink} from "react-router";
import {CreateRealmTenantDialog} from "./CreateRealmTenantDialog.tsx";

type Notice = {
    message: string;
    severity: "success" | "error" | "info" | "warning";
    open: boolean;
}

export default function RealmTenants({realmId, query=null, cursor=null, limit=10}: {realmId: string, query: string | null, cursor: string | null, limit: number}) {
    const [currentQuery, setCurrentQuery] = useState<string | null>(query);
    const [filter, setFilter] = useState<string>(currentQuery ?? "");
    const [currentCursor, setCursor] = useState<string | null>(cursor);
    const [currentLimit] = useState<number>(limit);
    const [isCreateRealmDialogOpen, setIsCreateRealmDialogOpen] = useState<boolean>(false);
    const [pages, setPages] = useState<(string | null)[]>([]);

    const searchTenants = useSearchRealmTenants({
        realmId,
        query: currentQuery,
        limit: currentLimit,
        cursor: currentCursor,
    })

    const searchTenantsCount = useSearchRealmTenantsCount({
        realmId,
        query: currentQuery,
        limit: currentLimit,
    })

    const [notice, setNotice] = useState<Notice>({
        message: "",
        severity: "success",
        open: false,
    })

    useEffect(() => {
        const id = setTimeout(() => {
            if(currentQuery != filter) {
                setCurrentQuery(filter);
                setCursor(null);
                setPages([]);
            }
        }, 300)

        return () => clearTimeout(id);
    }, [filter])

    function handleCloseNotice() {
        setNotice({
            message: "",
            severity: "success",
            open: false,
        })
    }

    function onTenantCreated() {
        setNotice({
            message: "Tenant created successfully",
            severity: "success",
            open: true,
        })

        setCursor(null);
    }

    const nextPageCursor = searchTenants.data?.cursor ?? null;

    function nextPage() {
        if(nextPageCursor) {
            setCursor(nextPageCursor);
            pages.push(currentCursor);
            setPages([...pages]);
        }
    }

    function previousPage() {
        if(pages.length > 0) {
            const previousPageCursor = pages.pop();

            if(previousPageCursor !== undefined) {
                setCursor(previousPageCursor);
                setPages([...pages]);
            }
        }
    }

    const tenantRows: ReactNode[] = searchTenants.data?.tenants?.map(item => (
        <TableRow key={item.id}>
            <TableCell>
                <Link component={RouterLink} to={`/realms/${item.id}`}>{item.id}</Link>
            </TableCell>
            <TableCell>{item.name}</TableCell>
            <TableCell>{item.slug}</TableCell>
        </TableRow>
    )) ?? [];

    return (
        <>
            <Snackbar
                open={notice.open}
                autoHideDuration={5000}
                onClose={handleCloseNotice}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert onClose={handleCloseNotice} severity={notice.severity} sx={{ width: '100%' }} variant="filled">
                    {notice.message}
                </Alert>
            </Snackbar>

            <Box
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginBottom: 2,
                    gap: 2,
                }}
            >
                <Typography variant="h5">Tenants</Typography>

                <Box sx={{ display: "flex", gap: 2 }}>
                    <TextField
                        label="Filter"
                        size="small"
                        value={filter}
                        onChange={(e) => setFilter(e.target.value)}
                        sx={{ width: 300 }}
                    />
                    <Button
                        variant="contained"
                        onClick={() => setIsCreateRealmDialogOpen(true)}
                    >
                        New Tenant
                    </Button>
                </Box>
            </Box>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Name</TableCell>
                            <TableCell>Slug</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tenantRows}
                    </TableBody>
                </Table>
            </TableContainer>
            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: 2 }}>
                <Button disabled={pages.length === 0} onClick={previousPage}>Previous</Button>
                <Box>{searchTenantsCount.data || 0} Items Total</Box>
                <Button disabled={nextPageCursor === null} onClick={nextPage}>Next</Button>
            </Box>
            <CreateRealmTenantDialog realmId={realmId} isOpen={isCreateRealmDialogOpen} onClose={() => setIsCreateRealmDialogOpen(false)} onCreate={onTenantCreated} />
        </>
    )
}