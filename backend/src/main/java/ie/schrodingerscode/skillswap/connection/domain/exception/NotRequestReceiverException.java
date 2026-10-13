package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

public class NotRequestReceiverException extends ForbiddenException {
    public NotRequestReceiverException() {
        super("Only the receiver can accept or decline this request");
    }
}
