-- One row per block. Only the blocker can remove it (unblock).
-- Unlike swap_requests, direction matters here: A blocking B and B blocking A are two separate rows,
-- so a plain unique constraint is enough, no LEAST/GREATEST index.
CREATE TABLE blocks (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    blocker_id BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    blocked_id BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (blocker_id <> blocked_id),
    UNIQUE (blocker_id, blocked_id)
);
