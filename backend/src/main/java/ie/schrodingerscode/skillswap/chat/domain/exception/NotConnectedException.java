package ie.schrodingerscode.skillswap.chat.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

/**
 * Thrown when someone messages a user they are not connected to.
 * It extends ForbiddenException, so GlobalExceptionHandler automatically
 * turns it into HTTP 403 with {"status": 403, "message": "You can only message
 * your connections"}.
 * This exception could be in application layer as it is thrown by SendMessage
 * (application service), but following structure doc exceptions are in domain.
 */
public class NotConnectedException extends ForbiddenException {
    public NotConnectedException() {
        super("You can only message your connections");
    }

}
