import {Form, LengthValidator, NULL, RegExValidator, useForm} from "../../shared/hooks/forms.ts";
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {useCreateAccount} from "../api/createAccount.ts";
import type Account from "../persistance/Account.ts";
import {useNotification} from "../../shared/components/notifications.tsx";

interface CreateAccountDialogProps {
    isOpen: boolean;
    setOpen?: (isOpen: boolean) => void;
    tenantId: string;
    onCreate?: (account: Account) => void;
}

const USERNAME_LENGTH = 255;
const FIRST_NAME_LENGTH = 255;
const LAST_NAME_LENGTH = 255;

export function CreateAccountDialog(
    {isOpen, setOpen, tenantId, onCreate}: CreateAccountDialogProps
) {
    const createAccount = useCreateAccount();
    const { showNotification } = useNotification();

    const form = useForm({
        onSubmit: async (form) => {
            return createAccount.mutate({
                tenantId,
                username: form.get("username"),
                firstName: form.get("firstName"),
                lastName: form.get("lastName"),
                email: form.get("email"),
                phone: form.get("phone"),
                enabled: true,
                attributes: {},
            }, {
                onSuccess: (response) => {
                    form.reset();
                    handleCloseDialog();
                    onCreate?.(response);
                    showNotification("Account created successfully!", "success");
                },

                onError: () => {
                    showNotification("Account couldn't be created!", "error");
                }
            });
        },
        fields: {
            username: {
                initial: "",
                required: true,
                nullable: false,
                validators: [
                    new LengthValidator({
                        min: 1,
                        max: 255
                    })
                ]
            },
            firstName: {
                initial: null,
                required: false,
                nullable: true,
                validators: [
                    new LengthValidator({
                        min: 1,
                        max: 255
                    })
                ],
                onEmpty: NULL,
            },
            lastName: {
                initial: null,
                required: false,
                nullable: true,
                validators: [
                    new LengthValidator({
                        min: 1,
                        max: 255
                    })
                ],
                onEmpty: NULL,
            },
            email: {
                initial: null,
                required: false,
                nullable: true,
                validators: [
                    new RegExValidator(
                        /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                        () => "Email address must be a valid email address"
                    )
                ],
                onEmpty: NULL,
            },
            phone: {
                initial: null,
                required: false,
                nullable: true,
                validators: [
                    new RegExValidator(
                        /^\+?[1-9]\d{1,14}$/,
                        () => "Phone number must be a valid E.164-style phone number"
                    )
                ],
                onEmpty: NULL,
            }
        }
    })

    const handleCloseDialog = () => {
        if(form.isSaving()) return;
        form.reset();
        setOpen?.(false);
    };

    return (
        <Dialog open={isOpen} onClose={handleCloseDialog} fullWidth maxWidth="md">
            <Box component="form" {...form.form()}>
                <DialogTitle>Create Account</DialogTitle>
                <DialogContent>
                    <CreateAccountDialogTextInput form={form} field="username" label="Username" required={true} maxLength={USERNAME_LENGTH} />
                    <CreateAccountDialogTextInput form={form} field="email" label="Email" required={false} maxLength={null} type="email" />
                    <CreateAccountDialogTextInput form={form} field="firstName" label="First Name" required={false} maxLength={FIRST_NAME_LENGTH} />
                    <CreateAccountDialogTextInput form={form} field="lastName" label="Last Name" required={false} maxLength={LAST_NAME_LENGTH} />
                    <CreateAccountDialogTextInput form={form} field="phone" label="Phone Number" required={false} />
                    <DialogActions>
                        <Button onClick={handleCloseDialog} disabled={form.isSaving()}>
                            Cancel
                        </Button>
                        <Button type="submit" variant="contained" disabled={!form.canSubmit()}>
                            Create
                        </Button>
                    </DialogActions>
                </DialogContent>
            </Box>
        </Dialog>
    )
}

interface CreateAccountDialogTextInputProps {
    field: string;
    label: string;
    required?: boolean;
    maxLength?: number | null;
    type?: string;
    form: Form
}

function CreateAccountDialogTextInput(
    {
        form, field, label, required = false, maxLength = null, type = "text"
    }: CreateAccountDialogTextInputProps
) {
    const slotProps: any = {required};

    if(maxLength != null) {
        slotProps.maxLength = maxLength;
    }

    return (
        <TextField
            label={label}
            variant="standard"
            fullWidth={true}
            type={type}
            helperText={
                maxLength != null ? `${form.get(field)?.length ?? 0}/${USERNAME_LENGTH}` : ''
            }
            slotProps={slotProps}
            {...form.field(field)}
        />
    )
}