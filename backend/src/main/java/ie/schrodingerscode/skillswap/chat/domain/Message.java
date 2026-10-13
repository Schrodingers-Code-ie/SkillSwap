package ie.schrodingerscode.skillswap.chat.domain;

import java.time.Instant;
import java.util.Objects;

/** Entity of chat message. */
public record Message(Long id, long senderId, long receiverId, MessageText text, Instant sentAt) {
    /**
     * Compact record constructor, if all checks pass, fields are assigned
     * automatically.
     * Prevents messages from being created with null timestamp.
     * If constructor throws NullPointerException no object is created preventing
     * invalid
     * Message from existing.
     */
    public Message {
        Objects.requireNonNull(sentAt, "sentAt must not be null");
    }

    /**
     * Creates a new unsaved message.
     */
    public static Message create(long senderId, long receiverId, MessageText text, Instant sentAt) {
        return new Message(null, senderId, receiverId, text, sentAt);
    }

    /**
     * Returns a copy with the given id.
     */
    public Message withId(long newId) {
        return new Message(newId, senderId, receiverId, text, sentAt);
    }

}
