package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * Thrown when someone signs up with an email that already has an account.
 * Returned as 409.
 */
public class EmailAlreadyUsedException extends ConflictException {

    public EmailAlreadyUsedException(String message) {
        super(message);
    }

}
