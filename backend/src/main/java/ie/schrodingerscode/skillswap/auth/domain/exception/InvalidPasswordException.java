package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when a password is empty or not 8 to 72 characters long.
 * Returned as 400.
 */
public class InvalidPasswordException extends ValidationException {

    public InvalidPasswordException(String message) {
        super(message);
    }

}
