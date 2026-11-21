package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when someone signs up without confirming they are 18 or older.
 * Returned as 400 by GlobalExceptionHandler.
 */
public class AgeNotConfirmedException extends ValidationException {

    public AgeNotConfirmedException(String message) {
        super(message);
    }

}
