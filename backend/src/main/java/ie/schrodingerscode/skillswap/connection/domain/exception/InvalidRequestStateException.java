package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * The request is in the wrong state for this, e.g. accepting a request that is already accepted. Gives 409.
 */
public class InvalidRequestStateException extends ConflictException {
    public InvalidRequestStateException(String message) {
        super(message);
    }
}
