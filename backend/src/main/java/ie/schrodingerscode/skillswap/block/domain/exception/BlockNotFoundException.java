package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

//404 on unblock. Also what a user gets if they try to remove a block someone else placed on them
public class BlockNotFoundException extends NotFoundException {
    public BlockNotFoundException() {
        super("You haven't blocked this user");
    }
}
