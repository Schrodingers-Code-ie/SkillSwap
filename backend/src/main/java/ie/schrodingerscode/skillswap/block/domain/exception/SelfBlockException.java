package ie.schrodingerscode.skillswap.block.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

//400, Block.create refuses it
public class SelfBlockException extends ValidationException {
    public SelfBlockException() {
        super("You can't block yourself");
    }
}
