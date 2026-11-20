package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * The user the request is sent to doesn't exist. Gives 404.
 */
public class ReceiverNotFoundException extends NotFoundException {
    public ReceiverNotFoundException() {
        super("User not found");
    }
}
