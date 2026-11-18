package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

//one of the two blocked the other. The message doesn't say who, so nobody finds out they were blocked
public class BlockedRequestException extends ForbiddenException {
    public BlockedRequestException() {
        super("You can't send a swap request to this user");
    }
}
