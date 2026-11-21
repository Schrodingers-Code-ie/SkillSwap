package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * There's no profile for this user id. Sent back as 404 Not Found.
 */

public class ProfileNotFoundException extends NotFoundException {
    public ProfileNotFoundException(long userId) {
        super("Profile for user " + userId + " not found");
    }
}
