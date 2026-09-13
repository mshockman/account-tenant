import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, TextField} from "@mui/material";
import {useNotification} from "../../shared/components/notifications.tsx";
import {RegExValidator, useForm} from "../../shared/hooks/forms.ts";
import type {CreateRealmTenantRequest, RealmTenantResponse} from "../api/realms.ts";
import {useCreateRealmTenant} from "../api/useRealms.ts";

interface CreateRealmTenantDialogProps {
    isOpen: boolean;
    onClose: () => void;
    realmId: string;
    onCreate?: (tenant: RealmTenantResponse) => void
}


export const TENANT_NAME_REGEX = /^[a-zA-Z_][a-zA-Z0-9_\- ]{0,99}$/;
export const TENANT_SLUG_REGEX = /^[a-zA-Z_][a-zA-Z0-9_\-]{0,49}$/;
export const TENANT_NAME_VALIDATION_ERROR = "Tenant name must be between 1 and 100 characters long and contain only letters, numbers, underscores, and dashes."
export const TENANT_SLUG_VALIDATION_ERROR = "Tenant slug must start with a letter and only contain letters, numbers, underscores, and dashes."


export function CreateRealmTenantDialog({isOpen, onClose, realmId, onCreate}: CreateRealmTenantDialogProps) {
    const createRealmTenant = useCreateRealmTenant();
    const { showNotification } = useNotification();

    const form = useForm({
        onSubmit: async (form) => {
            return createRealmTenant.mutate({
                realmId: realmId,
                enabled: true,
                ...form.validate()
            } as CreateRealmTenantRequest, {
                onSuccess: (response) => {
                    form.reset();
                    handleCloseDialog();
                    onCreate?.(response);
                },

                onError: () => {
                    showNotification("Something went wrong!", "error");
                }
            })
        },
        fields: {
            name: {
                initial: "",
                required: true,
                nullable: false,
                validators: [
                    new RegExValidator(TENANT_NAME_REGEX, () => TENANT_NAME_VALIDATION_ERROR)
                ]
            }
        }
    })

    function handleCloseDialog() {
        if(form.isSaving()) return;
        form.reset();
        onClose();
    }

    return (
        <Dialog open={isOpen} onClose={handleCloseDialog} fullWidth maxWidth="md">
            <Box component="form" {...form.form()}>
                <DialogTitle>Create Realm Tenant</DialogTitle>
                <DialogContent>
                    <FormControl fullWidth={true}>
                        <TextField
                            label="Tenant Name"
                            variant="standard"
                            fullWidth={true}
                            helperText={`${form.get("name").length}/100`}
                            slotProps={{
                                htmlInput: {
                                    required: true,
                                    maxLength: 100,
                                }
                            }}
                            {...form.field("name")}
                        />
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} disabled={form.isSaving()}>
                        Cancel
                    </Button>
                    <Button type="submit" variant="contained" disabled={!form.canSubmit()}>
                        Create
                    </Button>
                </DialogActions>
            </Box>
        </Dialog>
    )
}