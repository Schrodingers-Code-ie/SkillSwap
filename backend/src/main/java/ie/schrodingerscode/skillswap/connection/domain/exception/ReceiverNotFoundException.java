package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * The person you're sending it to doesn't exist (404).
 */
public class ReceiverNotFoundException extends NotFoundException {
    public ReceiverNotFoundException() {
        super("User not found");
    }
}
