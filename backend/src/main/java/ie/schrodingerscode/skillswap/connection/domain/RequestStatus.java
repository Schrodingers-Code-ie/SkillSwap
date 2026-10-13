package ie.schrodingerscode.skillswap.connection.domain;

public enum RequestStatus {
    PENDING,  // sent, waiting for the receiver to answer
    ACCEPTED  // the two users are now connected
    // there is no DECLINED status, declining deletes the request
}
