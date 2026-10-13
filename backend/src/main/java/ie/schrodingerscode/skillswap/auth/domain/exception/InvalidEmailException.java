package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class InvalidEmailException extends ValidationException {

    public InvalidEmailException(String message) {
        super(message);
    }

}
