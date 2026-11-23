package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * Thrown by {@code Block.create} when someone tries to block themselves.
 * Returned as 400.
 */
public class SelfBlockException extends ValidationException {
    public SelfBlockException() {
        super("You can't block yourself");
    }
}
