import {Box, Breadcrumbs, Tab, Tabs, Typography} from "@mui/material";
import {Link, redirect, useParams} from "react-router";
import * as React from "react";
import {requireDefined} from "../shared/assertions.ts";
import {useState} from "react";
import {useTenant} from "./api/useTenants.ts";
import {useGetRealm} from "../realms/api/useRealms.ts";
import {TenantEditor} from "./components/TenantEditor.tsx";
import {AccountTab} from "../accounts/components/AccountTab.tsx";


export function TenantPage() {
    const { id } = useParams();
    const [currentTab, setCurrentTab] = useState(0);

    if(!id) {
        redirect('/admin')
        return
    }

    const tenantGetter = useTenant(id)
    const realmGet = useGetRealm(tenantGetter.data?.realmId);

    if(tenantGetter.isLoading || realmGet.isLoading) return <div>Loading...</div>;

    const realmId = requireDefined(tenantGetter.data?.realmId);
    const realmName = requireDefined(realmGet.data?.name);

    const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
        setCurrentTab(newValue);
    }

    return (
        <Box>
            <Breadcrumbs sx={{ marginBottom: 2 }}>
                <Link to="/">Realms</Link>
                <Link to={`/realms/${realmId}`}>
                    {realmName}
                </Link>
                <Typography sx={{ color: "text.primary" }}>
                    {tenantGetter.data?.name ?? id}
                </Typography>
            </Breadcrumbs>
            <Tabs value={currentTab} onChange={handleTabChange} aria-label="Tenant Tabs" sx={{ marginBottom: 2 }}>
                <Tab label="Tenant" />
                <Tab label="Accounts" />
                <Tab label="Settings" />
            </Tabs>
            { currentTab === 0 && <TenantEditor tenant={tenantGetter.data!!} /> }
            { currentTab === 1 && <AccountTab tenantId={id} /> }
        </Box>
    )
}
