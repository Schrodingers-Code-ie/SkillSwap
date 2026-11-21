package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * Thrown when a user ID does not match any account. Returned as 404.
 */
public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
