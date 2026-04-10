import {Table, TableBody, TableCell, TableContainer, TableRow} from "@mui/material";


export function RealmPageHeader(data: any) {
    data = data.data;

    return (
        <>
            <h1>{data?.name}</h1>

            <TableContainer>
                <Table>
                    <TableBody>
                        <TableRow>
                            <TableCell>
                                Realm Name:
                            </TableCell>
                            <TableCell>
                                {data?.name}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Realm ID:
                            </TableCell>
                            <TableCell>
                                {data?.id}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Realm Slug:
                            </TableCell>
                            <TableCell>
                                {data?.slug}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Created At:
                            </TableCell>
                            <TableCell>
                                {data?.createdAt}
                            </TableCell>
                        </TableRow>
                        <TableRow>
                            <TableCell>
                                Updated At:
                            </TableCell>
                            <TableCell>
                                {data?.updatedAt}
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    )
}