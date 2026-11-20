package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

/**
 * Someone who isn't the sender tries to cancel. Gives 403.
 */
public class NotRequestSenderException extends ForbiddenException {
    public NotRequestSenderException() {
        super("Only the sender can cancel this request");
    }
}
