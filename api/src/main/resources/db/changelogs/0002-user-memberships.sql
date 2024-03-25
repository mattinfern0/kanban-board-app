CREATE TABLE IF NOT EXISTS app_user
(
    id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT app_user_pk
            PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS organization_role
(
    id SERIAL
        CONSTRAINT organization_role_pk
            PRIMARY KEY,
    codename TEXT NOT NULL
        CONSTRAINT organization_role_unique_codename
            UNIQUE
);

CREATE TABLE IF NOT EXISTS organization_users
(
    user_id uuid NOT NULL
        CONSTRAINT organization_users_user_id_fk
            REFERENCES app_user,
    organization_id uuid NOT NULL
        CONSTRAINT organization_users_organization_id_fk
            REFERENCES organization,
    role_id INTEGER NOT NULL
        CONSTRAINT organization_users_organization_role_id_fk
            REFERENCES organization_role,
    CONSTRAINT organization_users_pk
        PRIMARY KEY (organization_id, user_id)
);
