import type {Realm} from "../api/realms.ts";
import {Box, Button, TextField} from "@mui/material";
import {
    TENANT_NAME_REGEX,
    TENANT_NAME_VALIDATION_ERROR,
    TENANT_SLUG_REGEX,
    TENANT_SLUG_VALIDATION_ERROR
} from "./CreateRealmTenantDialog.tsx";
import {RegExValidator, useForm} from "../../shared/hooks/forms.ts";
import {useUpdateRealm} from "../api/useRealms.ts";
import {useNotification} from "../../shared/components/notifications.tsx";
import {slotProps} from "../../shared/slotProps.ts";


export function RealmEditor(
    {realm}: {
        realm: Realm
    }
) {
    const realmUpdater = useUpdateRealm()
    const { showNotification } = useNotification();

    const form = useForm({
        onSubmit: async (form) => {
            // noinspection JSUnusedGlobalSymbols
            return realmUpdater.mutate(
                {
                    id: realm.id,
                    ...form.validate(),
                } as any,
                {
                    onSuccess: (response) => {
                        form.setInitial("name", response.name);
                        form.setInitial("slug", response.slug);
                        form.reset();
                        showNotification("Realm updated successfully", "success");
                    },

                    onError: (error) => {
                        console.log(error);
                        showNotification("Something went wrong!", "error");
                    }
                }
            )
        },

        fields: {
            name: {
                initial: realm?.name ?? "",
                required: true,
                nullable: false,
                validators: [
                    new RegExValidator(TENANT_NAME_REGEX, () => TENANT_NAME_VALIDATION_ERROR)
                ]
            },

            slug: {
                initial: realm?.slug ?? "",
                required: true,
                nullable: false,
                validators: [
                    new RegExValidator(TENANT_SLUG_REGEX, () => TENANT_SLUG_VALIDATION_ERROR)
                ]
            }
        }
    })

    return (
        <Box sx={{ marginBottom: 2, padding: 2, flexGrow: 1, display: "flex", flexDirection: "column", gap: 5}} component="form" {...form.form()}>
            <Box>
                <TextField fullWidth={true} label="Realm ID" value={realm?.id ?? ""} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Created At" value={realm?.createdAt ?? ""} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Updated At" value={realm?.updatedAt ?? ""} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Name" {...form.field("name")} {...slotProps(100, true)} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Slug" {...form.field("slug")} {...slotProps(50, true)} />
            </Box>
            <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
                {/*<Button type="submit" variant="contained" sx={{marginRight: 1}} onClick={() => form.reset()}>Reset</Button>*/}
                <Button type="submit" disabled={!form.hasChanged() || !form.canSubmit()} variant="contained">Save</Button>
            </Box>
        </Box>
    )
}