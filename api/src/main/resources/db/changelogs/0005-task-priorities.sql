BEGIN;

CREATE TABLE IF NOT EXISTS task_priority
(
    id INTEGER PRIMARY KEY,
    codename TEXT NOT NULL
);

INSERT INTO task_priority (id, codename)
VALUES
    (1,'LOW'),
    (2,'MEDIUM'),
    (3,'HIGH');

ALTER TABLE task
    ADD COLUMN IF NOT EXISTS priority_id INTEGER NULL
        CONSTRAINT task_priority_id_fk
            REFERENCES task_priority;

COMMIT;