package ie.schrodingerscode.skillswap.user.domain;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

public class ProfileNotFoundException extends NotFoundException {
    public ProfileNotFoundException(long userId) {
        super("Profile for user " + userId + " not found");
    }
}