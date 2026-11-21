package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown when an email is empty, too long or not in the name@domain.tld format.
 * Returned as 400.
 */
public class InvalidEmailException extends ValidationException {

    public InvalidEmailException(String message) {
        super(message);
    }

}
