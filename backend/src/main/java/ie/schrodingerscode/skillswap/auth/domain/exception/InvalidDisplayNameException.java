package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when the display name is empty or longer than 100 characters.
 * Returned as 400.
 */
public class InvalidDisplayNameException extends ValidationException {

    public InvalidDisplayNameException(String message) {
        super(message);
    }

}
