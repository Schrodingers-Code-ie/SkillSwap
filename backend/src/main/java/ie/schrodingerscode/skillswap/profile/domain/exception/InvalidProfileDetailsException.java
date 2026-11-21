package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * The name or bio breaks a rule in Profile.updateDetails. Sent back as 400 Bad Request.
 */
public class InvalidProfileDetailsException extends ValidationException {
    public InvalidProfileDetailsException(String message) {
        super(message);
    }
}
