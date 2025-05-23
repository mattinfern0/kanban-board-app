BEGIN;

DROP INDEX IF EXISTS task_unique_column_order;

ALTER TABLE task
    DROP CONSTRAINT IF EXISTS unique_column_order;

ALTER TABLE task
    /* Constraint should be deferred to allow bulk updating order of multiple tasks */
    ADD CONSTRAINT unique_column_order UNIQUE (board_column_id, board_column_order) INITIALLY DEFERRED;

COMMIT;