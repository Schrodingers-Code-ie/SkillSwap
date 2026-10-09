package ie.schrodingerscode.skillswap.match.application.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
