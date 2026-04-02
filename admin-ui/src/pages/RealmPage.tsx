import {useParams} from "react-router";
import {useGetRealm, useSearchRealmTenants} from "../hooks/useRealms.ts";
import {RealmPageHeader} from "../components/RealmPageHeader.tsx";
import RealmTenants from "../components/RealmTenants.tsx";
import {Box} from "@mui/material";


export default function RealmPage() {
    const { id } = useParams();

    const realmGet = useGetRealm(id as string);

    return (
        <>
            <Box sx={{ marginBottom: 2 }}>
                <RealmPageHeader data={realmGet.data} />
            </Box>
            <RealmTenants realmId={id as string} query={null} cursor={null} limit={10} />
        </>
    )
}