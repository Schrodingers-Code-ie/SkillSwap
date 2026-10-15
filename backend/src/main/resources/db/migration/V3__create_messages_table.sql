CREATE TABLE messages (
    id          BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id   BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    receiver_id BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    text        TEXT        NOT NULL,
    sent_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT messages_sender_not_receiver CHECK (sender_id <> receiver_id)
);

CREATE INDEX messages_conversation_idx ON messages (
    LEAST(sender_id, receiver_id),
    GREATEST(sender_id, receiver_id),
    sent_at,
    id
);