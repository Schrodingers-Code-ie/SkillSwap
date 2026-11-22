package ie.schrodingerscode.skillswap.connection.domain;

/**
 * Status of a swap request. Declined or cancelled requests are deleted, so they don't need a status.
 */
public enum RequestStatus {
    PENDING, // sent, waiting for the receiver to answer
    ACCEPTED // the two users are now connected
}
