package ie.schrodingerscode.skillswap.connection.application;

/**
 * Small interface other features can use to ask "are these two users connected?".
 * Chat uses it so only connected users can message each other.
 */
public interface ConnectionChecker {

    boolean areConnected(long userA, long userB);
}
