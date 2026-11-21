package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * Thrown when someone signs up with a username that is already taken.
 * The check ignores case, so Bob and bob count as the same. Returned as 409.
 */
public class UsernameAlreadyUsedException extends ConflictException {

    public UsernameAlreadyUsedException(String message) {
        super(message);
    }

}
