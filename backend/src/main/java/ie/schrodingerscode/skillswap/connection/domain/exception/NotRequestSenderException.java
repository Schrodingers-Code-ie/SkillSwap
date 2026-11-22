package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

/**
 * Only the sender can cancel (403).
 */
public class NotRequestSenderException extends ForbiddenException {
    public NotRequestSenderException() {
        super("Only the sender can cancel this request");
    }
}
