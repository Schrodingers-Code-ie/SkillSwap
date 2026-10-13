package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException() {
        super("Swap request not found");
    }
}
