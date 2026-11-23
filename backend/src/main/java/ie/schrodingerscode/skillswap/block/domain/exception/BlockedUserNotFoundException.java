package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * Thrown when the user someone tries to block doesn't exist.
 * Returned as 404.
 */
public class BlockedUserNotFoundException extends NotFoundException {
    public BlockedUserNotFoundException() {
        super("User not found");
    }
}
