package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

//409, blocking the same person twice
public class AlreadyBlockedException extends ConflictException {
    public AlreadyBlockedException() {
        super("You have already blocked this user");
    }
}
