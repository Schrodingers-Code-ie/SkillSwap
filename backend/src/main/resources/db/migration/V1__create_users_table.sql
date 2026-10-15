CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    username      VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT username_format CHECK (username ~ '^[A-Za-z0-9_]{3,20}$')  -- username can contain letters A-Z, a-z, numbers 0-9 and _. Can range from 3 to 20 characters
);

CREATE UNIQUE INDEX username_idx ON users (lower(username));