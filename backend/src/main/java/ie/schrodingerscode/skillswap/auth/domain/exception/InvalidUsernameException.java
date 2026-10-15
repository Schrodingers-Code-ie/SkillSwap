package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class InvalidUsernameException extends ValidationException {

    public InvalidUsernameException(String message) {
        super(message);
    }

}
