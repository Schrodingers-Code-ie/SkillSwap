package ie.schrodingerscode.skillswap.chat.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when message text breaks a rule (empty, only whitespace, or too long).
 * Returned as 400 with this exception's message, because it extends {@link ValidationException}.
 */
public class InvalidMessageTextException extends ValidationException {

    /**
     * @param message the reason shown to the user
     */
    public InvalidMessageTextException(String message) {
        super(message);
    }
}
