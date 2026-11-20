package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * These two already have a request between them, pending or accepted (409).
 */
public class DuplicateRequestException extends ConflictException {
    public DuplicateRequestException() {
        super("A request between you and this user already exists");
    }
}
