package ie.schrodingerscode.skillswap.connection.application;

/**
 * Small interface other features can use to remove whatever is between two users.
 * Block uses it so blocking someone deletes the pending request or connection between you.
 */
public interface ConnectionRemover {

    //deletes the request (pending or accepted) between them, in either direction. Does nothing if there is none
    void removeBetween(long userA, long userB);
}
