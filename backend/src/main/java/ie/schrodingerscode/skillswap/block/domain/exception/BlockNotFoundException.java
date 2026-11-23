package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * Thrown on unblock when the user has not blocked this person.
 * Also what a user gets if they try to remove a block someone else placed on them.
 * Returned as 404.
 */
public class BlockNotFoundException extends NotFoundException {
    public BlockNotFoundException() {
        super("You haven't blocked this user");
    }
}
