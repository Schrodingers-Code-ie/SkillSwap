package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

/**
 * Someone who isn't the receiver tries to accept or decline. Gives 403.
 */
public class NotRequestReceiverException extends ForbiddenException {
    public NotRequestReceiverException() {
        super("Only the receiver can accept or decline this request");
    }
}
