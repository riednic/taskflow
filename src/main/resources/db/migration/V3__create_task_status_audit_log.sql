CREATE TABLE task_status_audit_log
(
    id          BIGSERIAL PRIMARY KEY,
    task_id     BIGINT      NOT NULL REFERENCES tasks (id),
    from_status task_status NOT NULL,
    to_status   task_status NOT NULL,
    changed_by  BIGINT      NOT NULL REFERENCES users (id),
    changed_at  TIMESTAMPTZ NOT NULL
);
