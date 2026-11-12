package ie.schrodingerscode.skillswap.chat.domain;

import java.util.List;

/**
 * Defines message storage operations.
 */
public interface MessageRepository {
    /**
     * Saves a message
     * Returns a new Message (built with withId) that has the id
     */
    Message save(Message message);

    /**
     * Returns messages between two users, oldest first.
     * Used in implementation of loading message history.
     */
    List<Message> findConversation(long userA, long userB);

    /**
     * Returns the newest message of every conversation this user is in:
     * one message per other user (connection), in no particular order.
     */
    List<Message> findLatestPerConversation(long userId);
}
