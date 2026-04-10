import {createContext, type ReactNode, useContext, useMemo, useState} from "react";
import {Alert, Snackbar} from "@mui/material";


export type NotificationContextValue = {
    showNotification: (message: string, severity?: NotificationSeverity) => void;
};


export type NotificationSeverity = "success" | "info" | "warning" | "error";

export const NotificationContext = createContext<NotificationContextValue | undefined>(undefined);


export function NotificationProvider(
    {children}: {children: ReactNode}
) {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState("");
    const [severity, setSeverity] = useState<NotificationSeverity>("success");

    function showNotification(
        nextMessage: string,
        nextSeverity: NotificationSeverity = "success"
    ) {
        setMessage(nextMessage);
        setSeverity(nextSeverity);
        setOpen(true);
    }

    const value = useMemo(
        () => ({ showNotification }),
        []
    );

    return (
        <NotificationContext.Provider value={value}>
            {children}

            <Snackbar
                open={open}
                autoHideDuration={5000}
                onClose={() => setOpen(false)}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert
                    onClose={() => setOpen(false)}
                    severity={severity}
                    variant="filled"
                    sx={{ width: "100%" }}
                >
                    {message}
                </Alert>
            </Snackbar>
        </NotificationContext.Provider>
    )
}

export function useNotification() {
    const context = useContext(NotificationContext);

    if (!context) {
        throw new Error("useNotification must be used inside NotificationProvider");
    }

    return context;
}