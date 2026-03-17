import {useParams} from "react-router";
import {useGetRealm} from "../hooks/useRealms.ts";
import {Table, TableBody, TableCell, TableContainer, TableRow} from "@mui/material";


export default function RealmPage() {
    const { id } = useParams();

    const realmGet = useGetRealm(id as string);

    if(realmGet.isLoading) {
        return <h1>Loading...</h1>
    } else if(realmGet.isError) {
        return <h1>Error</h1>
    }

    return (
        <>
        <h1>{realmGet.data?.name}</h1>

            <TableContainer>
                <Table>
                    <TableBody>
                        <TableRow>
                            <TableCell>
                                Realm Name:
                            </TableCell>
                            <TableCell>
                                {realmGet.data?.name}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Realm ID:
                            </TableCell>
                            <TableCell>
                                {realmGet.data?.id}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Realm Slug:
                            </TableCell>
                            <TableCell>
                                {realmGet.data?.slug}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Created At:
                            </TableCell>
                            <TableCell>
                                {realmGet.data?.createdAt}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Updated At:
                            </TableCell>
                            <TableCell>
                                {realmGet.data?.updatedAt}
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    )
}