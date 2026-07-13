CREATE EXTENSION IF NOT EXISTS citext;

CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'MEMBER',
    'REVIEWER'
    );

CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         CITEXT       NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          user_role    NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL
);