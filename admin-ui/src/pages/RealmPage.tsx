import {useParams} from "react-router";


export default function RealmPage() {
    const { id } = useParams();

    return (
        <h1>Realm {`${id}`}</h1>
    )
}