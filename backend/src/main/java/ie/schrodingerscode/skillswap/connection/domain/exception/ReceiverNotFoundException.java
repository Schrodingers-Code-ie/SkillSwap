package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

//the user the request is sent to doesn't exist
public class ReceiverNotFoundException extends NotFoundException {
    public ReceiverNotFoundException() {
        super("User not found");
    }
}
