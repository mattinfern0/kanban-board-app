CREATE TABLE organization_invite_statuses
(
    id SERIAL PRIMARY KEY,
    codename TEXT NOT NULL
);

INSERT INTO organization_invite_statuses (id, codename)
VALUES (1, 'PENDING'),
       (2, 'ACCEPTED'),
       (3, 'REVOKED');

ALTER TABLE organization_invites
    ADD COLUMN status_id INTEGER
        CONSTRAINT fk_organizationinvites_statusid REFERENCES organization_invite_statuses;

ALTER TABLE organization_invites
    ALTER COLUMN status_id SET NOT NULL;
