package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class InvalidProfileDetailsException extends ValidationException {
    public InvalidProfileDetailsException(String message) {
        super(message);
    }
}
