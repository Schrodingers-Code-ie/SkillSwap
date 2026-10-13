package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class AgeNotConfirmedException extends ValidationException {

    public AgeNotConfirmedException(String message) {
        super(message);
    }

}
