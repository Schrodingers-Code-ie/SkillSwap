package ie.schrodingerscode.skillswap.chat.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * A chat message from one user to another.
 *
 * <p>An entity: it is identified by its id, so two messages with the same text are still different
 * messages. The id is null until the message is saved. The text is always a valid
 * {@link MessageText}, and {@code sentAt} is never null, so an invalid message can't exist.
 *
 * @param id         database id, or null for a message that is not saved yet
 * @param senderId   id of the user who sent the message
 * @param receiverId id of the user the message was sent to
 * @param text       the validated message text
 * @param sentAt     when the message was sent
 */
public record Message(Long id, long senderId, long receiverId, MessageText text, Instant sentAt) {

    /**
     * Rejects a message without a timestamp.
     *
     * @throws NullPointerException if {@code sentAt} is null
     */
    public Message {
        Objects.requireNonNull(sentAt, "sentAt must not be null");
    }

    /**
     * Creates a new message that is not saved yet (no id).
     *
     * @param senderId   id of the user sending the message
     * @param receiverId id of the user receiving the message
     * @param text       the validated message text
     * @param sentAt     when the message was sent
     * @return the unsaved message
     */
    public static Message create(long senderId, long receiverId, MessageText text, Instant sentAt) {
        return new Message(null, senderId, receiverId, text, sentAt);
    }

    /**
     * Returns a copy of this message with the id the database gave it.
     *
     * @param newId the id assigned when the message was saved
     * @return the same message with that id
     */
    public Message withId(long newId) {
        return new Message(newId, senderId, receiverId, text, sentAt);
    }

    /**
     * Returns the id of the other person in this message.
     * For a message between 1 and 5, otherUser(1) is 5 and otherUser(5) is 1.
     *
     * @param me the user asking
     * @return the id of the other user
     * @throws IllegalArgumentException if me is neither the sender nor the receiver
     */
    public long otherUser(long me) {
        if (senderId == me) {
            return receiverId;
        }
        if (receiverId == me) {
            return senderId;
        }
        throw new IllegalArgumentException("User " + me + " is not part of this message");
    }

}
