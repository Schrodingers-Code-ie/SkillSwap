package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * There's no request with this id (404).
 */
public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException() {
        super("Swap request not found");
    }
}
