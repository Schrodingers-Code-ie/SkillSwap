package ie.schrodingerscode.skillswap.chat.domain;

import java.util.List;

/**
 * Storage for chat messages, as the domain and application layers see it.
 *
 * <p>An interface so the use cases don't depend on JPA: the real implementation is
 * {@code JpaMessageRepository}, and tests use an in-memory one. A conversation is the same no matter
 * which user is passed first.
 */
public interface MessageRepository {

    /**
     * Saves a new message.
     *
     * @param message the message to save, without an id
     * @return a copy of the message with the id the database gave it
     */
    Message save(Message message);

    /**
     * Returns every message between two users, in both directions, oldest first.
     *
     * @param userA one user in the conversation
     * @param userB the other user
     * @return the conversation, oldest first; empty if they have never messaged
     */
    List<Message> findConversation(long userA, long userB);

    /**
     * Returns the newest message of every conversation this user is in:
     * one message per other user, in no particular order.
     *
     * @param userId the user whose conversations to look at
     * @return one message per conversation; empty if the user has never messaged anyone
     */
    List<Message> findLatestPerConversation(long userId);
}
