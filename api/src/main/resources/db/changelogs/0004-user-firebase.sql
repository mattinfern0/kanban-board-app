BEGIN;

ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS firebase_id uuid;

COMMIT;