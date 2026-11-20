package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * Wrong state for this action, e.g. accepting a request that's already accepted (409).
 */
public class InvalidRequestStateException extends ConflictException {
    public InvalidRequestStateException(String message) {
        super(message);
    }
}
