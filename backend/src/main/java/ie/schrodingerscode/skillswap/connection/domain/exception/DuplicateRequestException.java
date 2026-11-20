package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * There is already a request between these two users (pending or accepted). Gives 409.
 */
public class DuplicateRequestException extends ConflictException {
    public DuplicateRequestException() {
        super("A request between you and this user already exists");
    }
}
