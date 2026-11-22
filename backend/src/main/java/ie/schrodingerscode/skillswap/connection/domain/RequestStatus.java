package ie.schrodingerscode.skillswap.connection.domain;

/**
 * A request is either still waiting or accepted, there's no declined.
 */
public enum RequestStatus {
    PENDING, // sent, waiting for the receiver to answer
    ACCEPTED // the two users are now connected
    // there is no DECLINED status, declining deletes the request
}
