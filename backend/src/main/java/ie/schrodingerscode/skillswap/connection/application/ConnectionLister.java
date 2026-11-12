package ie.schrodingerscode.skillswap.connection.application;

import java.util.List;

/**
 * Small interface other features can use to get the ids of a user's connections.
 * Chat uses it to build the chat list (GET /api/chats).
 */

public interface ConnectionLister {
    /**
     * @param userId the user whose connections we want
     * @return ids of everyone this user is connected with, in no particular order
     */
    List<Long> connectionIdsOf(long userId);
}
