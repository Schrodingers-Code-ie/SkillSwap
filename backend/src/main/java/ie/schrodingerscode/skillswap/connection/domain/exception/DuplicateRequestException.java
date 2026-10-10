package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

public class DuplicateRequestException extends ConflictException {
    public DuplicateRequestException() {
        super("A request between you and this user already exists");
    }
}
