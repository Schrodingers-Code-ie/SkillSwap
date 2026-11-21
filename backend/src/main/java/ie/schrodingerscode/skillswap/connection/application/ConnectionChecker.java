package ie.schrodingerscode.skillswap.connection.application;

/**
 * Small interface other features can use to ask "are these two users connected?".
 * Chat uses it so only connected users can message each other.
 */
public interface ConnectionChecker {

    /**
     * Checks if two users are friends.
     *
     * @param userA one user
     * @param userB the other user, order doesn't matter
     * @return true if they have an accepted request between them
     */
    boolean areConnected(long userA, long userB);
}
