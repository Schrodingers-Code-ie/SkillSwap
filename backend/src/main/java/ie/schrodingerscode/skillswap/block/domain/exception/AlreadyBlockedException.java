package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * Thrown when someone blocks a user they have already blocked.
 * Returned as 409.
 */
public class AlreadyBlockedException extends ConflictException {
    public AlreadyBlockedException() {
        super("You have already blocked this user");
    }
}
