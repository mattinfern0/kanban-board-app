BEGIN;

CREATE TABLE IF NOT EXISTS task_assignees
(
    task_id uuid NOT NULL
        CONSTRAINT task_assignee_task_id_fk
            REFERENCES task,
    user_id uuid NOT NULL
        CONSTRAINT task_assignee_app_user_id_fk
            REFERENCES app_user,
    CONSTRAINT task_assignee_pk
        PRIMARY KEY (task_id, user_id)
);

COMMIT;