package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

/**
 * One of the two blocked the other. The message doesn't say who, so nobody finds out they were blocked.
 */
public class BlockedRequestException extends ForbiddenException {
    public BlockedRequestException() {
        super("You can't send a swap request to this user");
    }
}
