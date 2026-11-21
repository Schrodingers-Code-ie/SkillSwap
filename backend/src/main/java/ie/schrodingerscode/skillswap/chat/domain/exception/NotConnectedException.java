package ie.schrodingerscode.skillswap.chat.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ForbiddenException;

/**
 * Thrown when a user sends a message to, or reads the history with, someone they are not connected to.
 * Returned as 403, because it extends {@link ForbiddenException}.
 *
 * <p>It is thrown by the application layer, but it lives in {@code domain.exception} like every
 * other feature's exceptions, following the team's structure doc.
 */
public class NotConnectedException extends ForbiddenException {

    public NotConnectedException() {
        super("You can only message your connections");
    }

}
