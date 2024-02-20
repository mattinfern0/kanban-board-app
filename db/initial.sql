CREATE TABLE organization
(
    id           uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT organization_pk
            PRIMARY KEY,
    display_name TEXT                           NOT NULL
);

ALTER TABLE organization
    OWNER TO dev;

CREATE TABLE board
(
    id       uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT board_pk
            PRIMARY KEY,
    title    TEXT                           NOT NULL,
    owner_id uuid                           NOT NULL
        CONSTRAINT board_organization_id_fk
            REFERENCES organization
);

ALTER TABLE board
    OWNER TO dev;

CREATE TABLE board_column
(
    id            uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT board_column_pk
            PRIMARY KEY,
    board_id      uuid                           NOT NULL
        CONSTRAINT board_column_board_id_fk
            REFERENCES board
            ON DELETE RESTRICT,
    title         TEXT                           NOT NULL,
    display_order INTEGER                        NOT NULL
        CONSTRAINT board_column_display_order_positive
            CHECK (display_order >= 0)
);

ALTER TABLE board_column
    OWNER TO dev;

CREATE TABLE task
(
    id              uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT task_pk
            PRIMARY KEY,
    owner_id        uuid                           NOT NULL
        CONSTRAINT task_organization_id_fk
            REFERENCES organization,
    board_column_id uuid
        CONSTRAINT task_board_column_id_fk
            REFERENCES board_column,
    title           TEXT                           NOT NULL,
    description     TEXT                           NOT NULL
);

ALTER TABLE task
    OWNER TO dev;


