package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

//404, the user you're trying to block doesn't exist
public class BlockedUserNotFoundException extends NotFoundException {
    public BlockedUserNotFoundException() {
        super("User not found");
    }
}
