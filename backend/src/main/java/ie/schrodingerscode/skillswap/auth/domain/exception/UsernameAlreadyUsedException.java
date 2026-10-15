package ie.schrodingerscode.skillswap.auth.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

public class UsernameAlreadyUsedException extends ConflictException {

    public UsernameAlreadyUsedException(String message) {
        super(message);
    }

}
