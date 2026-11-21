package ie.schrodingerscode.skillswap.match.application.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * The user doesn't exist.
 */
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
