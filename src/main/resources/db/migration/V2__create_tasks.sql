CREATE TYPE task_status AS ENUM (
    'TODO',
    'IN_PROGRESS',
    'IN_REVIEW',
    'DONE',
    'CANCELLED',
    'REJECTED'
    );

CREATE TYPE task_priority AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH'
    );

CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255)  NOT NULL,
    description VARCHAR(1000),
    status      task_status   NOT NULL,
    priority    task_priority NOT NULL,
    assigned_to BIGINT REFERENCES users (id),
    version     BIGINT        NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL,
    updated_at  TIMESTAMPTZ   NOT NULL
);
