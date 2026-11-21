package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when a username is empty, not 3 to 20 characters, or has characters
 * other than letters, numbers and underscore. Returned as 400.
 */
public class InvalidUsernameException extends ValidationException {

    public InvalidUsernameException(String message) {
        super(message);
    }

}
