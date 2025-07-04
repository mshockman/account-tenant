--liquibase formated sql
--changeset Matthew Shockman:1

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE OR REPLACE FUNCTION update_modified_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ language 'plpgsql';

--rollback DROP FUNCTION IF EXISTS update_modified_column();
--rollback DROP EXTENSION IF EXISTS "uuid-ossp";