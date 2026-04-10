import {Link, useParams} from "react-router";
import {Box, Breadcrumbs, Tab, Tabs, Typography} from "@mui/material";
import {useState} from "react";
import * as React from "react";
import {useGetRealm} from "./api/useRealms.ts";
import {RealmEditor} from "./components/RealmEditor.tsx";
import RealmTenants from "../realms/components/RealmTenants.tsx";
import {RealmSettings} from "./components/RealmSettings.tsx";


export default function RealmPage() {
    const { id } = useParams();
    const [currentTab, setCurrentTab] = useState(0);

    const realmGet = useGetRealm(id as string);

    const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
        setCurrentTab(newValue);
    }

    if(realmGet.isLoading) return <div>Loading...</div>;

    return (
        <Box>
            <Breadcrumbs sx={{ marginBottom: 2 }}>
                <Link to="/">Realms</Link>
                <Typography sx={{ color: "text.primary" }}>
                    {realmGet.data?.name ?? id}
                </Typography>
            </Breadcrumbs>
            <Tabs value={currentTab} onChange={handleTabChange} aria-label="Realm Tabs" sx={{ marginBottom: 2 }}>
                <Tab label="Realm" />
                <Tab label="Tenants" />
                <Tab label="Settings" />
            </Tabs>
            {currentTab === 0 && <RealmEditor realm={realmGet.data!!} />}
            {currentTab === 1 && <RealmTenants realmId={id as string} query={null} cursor={null} limit={10} />}
            {currentTab === 2 && <RealmSettings id={id as string} />}
        </Box>
    )
}