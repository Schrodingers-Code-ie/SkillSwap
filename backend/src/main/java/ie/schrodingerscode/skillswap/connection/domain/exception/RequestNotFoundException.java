package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * No swap request with this id. Gives 404.
 */
public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException() {
        super("Swap request not found");
    }
}
