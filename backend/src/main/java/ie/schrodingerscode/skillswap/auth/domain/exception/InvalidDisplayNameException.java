package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class InvalidDisplayNameException extends ValidationException {

    public InvalidDisplayNameException(String message) {
        super(message);
    }

}
