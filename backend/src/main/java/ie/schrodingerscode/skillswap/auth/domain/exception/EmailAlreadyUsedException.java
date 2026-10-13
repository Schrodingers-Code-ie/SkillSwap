package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

public class EmailAlreadyUsedException extends ConflictException {

    public EmailAlreadyUsedException(String message) {
        super(message);
    }

}
