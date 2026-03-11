import {useCreateRealm, useRealms} from "../hooks/useRealms.ts";
import {
    Alert,
    Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormLabel, Link,
    Paper, Snackbar,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography
} from "@mui/material";
import {useMemo, useState} from "react";
import {RegExpValidator, useValidatedState} from "../hooks/useValidatedState.ts";
import {Link as RouterLink} from "react-router";


const REALM_NAME_REGEX = /^[a-zA-Z_][a-zA-Z0-9_\- ]{0,99}$/;
const REALM_SLUG_REGEX = /^[a-zA-Z_][a-zA-Z0-9_\-]{0,49}$/;
const REALM_NAME_VALIDATION_ERROR = "Realm name must be between 1 and 100 characters long and contain only letters, numbers, underscores, and dashes."
const REALM_SLUG_VALIDATION_ERROR = "Realm slug must be between 1 and 50 characters long and start with a letter or underscore and contain only letters, numbers, underscores, and dashes."

type Notice = {
    message: string;
    severity: "success" | "error" | "info" | "warning";
    open: boolean;
}

export default function Dashboard() {
    const realmsQuery = useRealms();
    const [search, setSearch] = useState("");
    const filteredRows = useMemo(() => {
        const value = search.trim().toLowerCase();

        if(!value) {
            return realmsQuery.data;
        }

        return realmsQuery.data?.filter(realm => {
            return realm.name.toLowerCase().includes(value) || realm.slug.toLowerCase().includes(value) || realm.id.toLowerCase().includes(value)
        });
    }, [realmsQuery.data, search])
    const [createOpen, setCreateOpen] = useState(false);
    const [saving, setSaving] = useState(false);

    const [realmName, setRealmName, error] = useValidatedState<string>("", [
        new RegExpValidator(REALM_NAME_VALIDATION_ERROR, REALM_NAME_REGEX),
    ])

    const [realmSlug, setRealmSlug, realmSlugError] = useValidatedState<string>("", [
        new RegExpValidator(REALM_SLUG_VALIDATION_ERROR, REALM_SLUG_REGEX),
    ])

    const createRealm = useCreateRealm();

    const [notice, setNotice] = useState<Notice>({
        message: "",
        severity: "success",
        open: false,
    })

    function handleCreateSubmit(event: any) {
        event.preventDefault();
        setSaving(true);
        createRealm.mutate({
            name: realmName,
            slug: realmSlug
        }, {
            onSuccess: () => {
                setSaving(false);
                setNotice({
                    message: "Realm created successfully",
                    severity: "success",
                    open: true,
                })
                setCreateOpen(false)
            }
        })
    }

    function handleCloseDialog() {
        if (saving) return;
        setCreateOpen(false);
        setRealmName("");
    }

    function handleCloseNotice() {
        setNotice({
            message: "",
            severity: "success",
            open: false,
        })
    }

    return (
        <div>
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

            <h1>Admin Dashboard</h1>

            <Box
            sx={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                marginBottom: 2,
                gap: 2,
            }}
            >
                <Typography variant="h5">Realms</Typography>

                <Box sx={{ display: "flex", gap: 2 }}>
                    <TextField
                        label="Filter"
                        size="small"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        sx={{ width: 300 }}
                    />
                    <Button variant="contained" onClick={() => setCreateOpen(true)}>
                        New Realm
                    </Button>
                </Box>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Slug</TableCell>
                            <TableCell>ID</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredRows?.map(realm => (
                            <TableRow key={realm.id}>
                                <TableCell>
                                    <Link component={RouterLink} to={`/realms/${realm.id}`}>{realm.name}</Link>
                                </TableCell>
                                <TableCell>{realm.slug}</TableCell>
                                <TableCell>{realm.id}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog open={createOpen} onClose={() => setCreateOpen(false)} fullWidth maxWidth="md">
                <form onSubmit={handleCreateSubmit}>
                    <DialogTitle>Create Realm</DialogTitle>
                    <DialogContent>
                        <Box sx={{ display: "flex", flexDirection: "column", gap: 1, marginBottom: 2 }}>
                            <FormLabel>*Realm name:</FormLabel>
                            <TextField
                                autoFocus
                                margin="dense"
                                fullWidth
                                value={realmName}
                                error={!!error}
                                helperText={error || `${realmName.length}/100`}
                                onChange={(e) => {
                                    setRealmName(e.target.value as string)
                                }}
                                slotProps={{
                                    htmlInput: {
                                        required: true,
                                        maxLength: 100,
                                    }
                                }}
                            />
                        </Box>
                        <Box sx={{ display: "flex", flexDirection: "column", gap: 1, marginBottom: 2 }}>
                            <FormLabel>*Realm Slug:</FormLabel>
                            <TextField
                                autoFocus
                                margin="dense"
                                fullWidth
                                value={realmSlug}
                                error={!!realmSlugError}
                                helperText={realmSlugError || `${realmSlug.length}/50`}
                                slotProps={{
                                    htmlInput: {
                                        required: true,
                                        maxLength: 50,
                                    }
                                }}
                                onChange={(e) => {
                                    setRealmSlug(e.target.value as string)
                                }}
                            />
                        </Box>
                    </DialogContent>

                    <DialogActions>
                        <Button onClick={handleCloseDialog} disabled={saving}>
                            Cancel
                        </Button>
                        <Button type="submit" variant="contained" disabled={saving}>
                            Create
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
        </div>
    )
}