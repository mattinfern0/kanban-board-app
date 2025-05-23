CREATE TABLE organization_invites
(
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    organization_id UUID NOT NULL REFERENCES organization,
    email TEXT,
    token TEXT UNIQUE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
