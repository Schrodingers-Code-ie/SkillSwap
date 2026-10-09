package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class InvalidPasswordException extends ValidationException {

    public InvalidPasswordException(String message) {
        super(message);
    }

}
