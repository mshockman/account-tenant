

export default interface Account {
    id: string;
    tenantId: string;
    tenantName: string;
    realmId: string;
    realmName: string;
    username: string;
    firstName?: string | null;
    lastName?: string | null;
    email?: string | null;
    phone?: string | null;
    enabled: boolean;
    attributes: Record<string, unknown>;
    createdAt: string;
    updatedAt: string;
    remoteId?: string | null;
}