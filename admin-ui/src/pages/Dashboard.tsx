import {
    Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormLabel, Link,
    Paper,
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
import {Link as RouterLink} from "react-router";
import {useCreateRealm, useRealms} from "../realms/api/useRealms.ts";
import {RegExpValidator, useValidatedState} from "../shared/hooks/useValidatedState.ts";
import {useNotification} from "../shared/components/notifications.tsx";


const REALM_NAME_REGEX = /^[a-zA-Z_][a-zA-Z0-9_\- ]{0,99}$/;
const REALM_NAME_VALIDATION_ERROR = "Realm name must be between 1 and 100 characters long and contain only letters, numbers, underscores, and dashes."

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

    const createRealm = useCreateRealm();

    const { showNotification } = useNotification();

    function handleCreateSubmit(event: any) {
        event.preventDefault();
        setSaving(true);
        createRealm.mutate({
            name: realmName,
        }, {
            onSuccess: () => {
                setSaving(false);
                showNotification("Realm created successfully!", "success");
                setCreateOpen(false)
            }
        })
    }

    function handleCloseDialog() {
        if (saving) return;
        setCreateOpen(false);
        setRealmName("");
    }

    return (
        <div>
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
                            <TableCell>ID</TableCell>
                            <TableCell>Name</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredRows?.map(realm => (
                            <TableRow key={realm.id}>
                                <TableCell>{realm.id}</TableCell>
                                <TableCell>
                                    <Link component={RouterLink} to={`/realms/${realm.id}`}>{realm.name}</Link>
                                </TableCell>
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