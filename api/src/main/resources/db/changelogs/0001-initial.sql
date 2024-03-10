-- changeset mattinfern0:1
BEGIN;

CREATE TABLE organization
(
    id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT organization_pk
            PRIMARY KEY,
    display_name TEXT NOT NULL
);

CREATE TABLE board
(
    id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT board_pk
            PRIMARY KEY,
    title TEXT NOT NULL,
    organization_id uuid NOT NULL
        CONSTRAINT board_organization_id_fk
            REFERENCES organization
);

CREATE TABLE task_status
(
    id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT task_status_pk
            PRIMARY KEY,
    codename TEXT NOT NULL
        CONSTRAINT task_status_unique_codename
            UNIQUE
);

CREATE TABLE board_column
(
    id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT board_column_pk
            PRIMARY KEY,
    board_id uuid NOT NULL
        CONSTRAINT board_column_board_id_fk
            REFERENCES board
            ON DELETE RESTRICT,
    title TEXT NOT NULL,
    display_order INTEGER NOT NULL
        CONSTRAINT board_column_display_order_positive
            CHECK (display_order >= 0),
    task_status_id uuid NOT NULL
        CONSTRAINT board_column_task_status_id_fk
            REFERENCES task_status
);

CREATE TABLE task
(
    id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT task_pk
            PRIMARY KEY,
    organization_id uuid NOT NULL
        CONSTRAINT task_organization_id_fk
            REFERENCES organization,
    board_column_id uuid
        CONSTRAINT task_board_column_id_fk
            REFERENCES board_column,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    task_status_id uuid NOT NULL
        CONSTRAINT task_task_status_id_fk
            REFERENCES task_status,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    board_column_order INTEGER
        CONSTRAINT task_board_column_order_gte_zero
            CHECK (board_column_order >= 0),
    CONSTRAINT task_board_column_and_board_column_order_both_null_or_not_null
        CHECK (((board_column_id IS NOT NULL) AND (board_column_order IS NOT NULL)) OR
            ((board_column_id IS NULL) AND (board_column_order IS NULL)))
);

CREATE UNIQUE INDEX task_unique_column_order
    ON task (board_column_id, board_column_order)
    WHERE ((board_column_id IS NOT NULL) AND (board_column_order IS NOT NULL));

COMMIT;
