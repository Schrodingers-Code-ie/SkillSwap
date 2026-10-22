-- One table for both pending requests and accepted connections, the status column says which.
-- Declining deletes the row, so there is no DECLINED status.
CREATE TABLE swap_requests (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id   BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    receiver_id BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status      VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (sender_id <> receiver_id)
);

-- Only one row per pair of users, whichever way the request was sent, so A->B and B->A can't both exist.
-- The domain already checks this, the index is a backstop for two requests arriving at the same moment.
CREATE UNIQUE INDEX swap_requests_user_pair_unique
    ON swap_requests (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id));
