import {Link, useParams} from "react-router";
import {useGetRealm} from "../hooks/useRealms.ts";
import {RealmPageHeader} from "../components/RealmPageHeader.tsx";
import RealmTenants from "../components/RealmTenants.tsx";
import {Box, Breadcrumbs, Tab, Tabs, Typography} from "@mui/material";
import {useState} from "react";


export default function RealmPage() {
    const { id } = useParams();
    const [currentTab, setCurrentTab] = useState(0);

    const realmGet = useGetRealm(id as string);

    const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
        setCurrentTab(newValue);
    }

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
            </Tabs>
            {currentTab === 0 && <Box sx={{ marginBottom: 2 }}><RealmPageHeader data={realmGet.data} /></Box>}
            {currentTab === 1 && <RealmTenants realmId={id as string} query={null} cursor={null} limit={10} />}
        </Box>
    )
}