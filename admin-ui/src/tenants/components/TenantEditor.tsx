import {Box, Button, TextField} from "@mui/material";
import type {TenantResponse} from "../api/TenantResponse.ts";
import {RegExValidator, useForm} from "../../shared/hooks/forms.ts";
import {
    TENANT_NAME_REGEX,
    TENANT_NAME_VALIDATION_ERROR,
    TENANT_SLUG_REGEX, TENANT_SLUG_VALIDATION_ERROR
} from "../../realms/components/CreateRealmTenantDialog.tsx";
import {slotProps} from "../../shared/slotProps.ts";


export function TenantEditor(
    {
        tenant
    }: {
        tenant: TenantResponse
    }
) {
    const form = useForm({
        onSubmit: async (form) => {

        },
        fields: {
            name: {
                initial: tenant.name,
                required: true,
                nullable: false,
                validators: [
                    new RegExValidator(TENANT_NAME_REGEX, () => TENANT_NAME_VALIDATION_ERROR)
                ]
            }
        }
    })


    return (
        <Box sx={{ marginBottom: 2, padding: 2, flexGrow: 1, display: "flex", flexDirection: "column", gap: 5}} component="form" {...form.form()}>
            <Box>
                <TextField fullWidth={true} label="Tenant ID" value={tenant.id} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Created At" value={tenant.createdAt ?? ""} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Updated At" value={tenant.updatedAt ?? ""} />
            </Box>
            <Box>
                <TextField fullWidth={true} label="Name" {...form.field("name")} {...slotProps(100, true)} />
            </Box>
            <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
                {/*<Button type="submit" variant="contained" sx={{marginRight: 1}} onClick={() => form.reset()}>Reset</Button>*/}
                <Button type="submit" disabled={!form.hasChanged() || !form.canSubmit()} variant="contained">Save</Button>
            </Box>
        </Box>
    )
}