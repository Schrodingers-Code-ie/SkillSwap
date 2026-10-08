package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

//e.g. accepting a request that is already accepted
public class InvalidRequestStateException extends ConflictException {
    public InvalidRequestStateException(String message) {
        super(message);
    }
}
