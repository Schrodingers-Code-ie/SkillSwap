package ie.schrodingerscode.skillswap.chat.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when message text breaks a rule (empty, or too long).
 * It extends ValidationException, so GlobalExceptionHandler automatically
 * turns it into HTTP 400 with {"status": 400, "message": "<our message>"}.
 */
public class InvalidMessageTextException extends ValidationException {
    public InvalidMessageTextException(String message) {
        super(message);
    }
}
