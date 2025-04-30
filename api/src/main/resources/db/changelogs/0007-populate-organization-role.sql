BEGIN;

INSERT INTO organization_role (id, codename)
VALUES
    (1,'OWNER'),
    (2,'MEMBER');
COMMIT;