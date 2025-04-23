BEGIN;

ALTER TABLE organization
ADD COLUMN personal_for_user_id uuid NULL
    CONSTRAINT organization_personal_for_user_id_fk REFERENCES app_user
    CONSTRAINT organization_unique_personal_fk UNIQUE;

COMMIT;