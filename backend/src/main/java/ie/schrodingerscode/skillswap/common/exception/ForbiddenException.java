package ie.schrodingerscode.skillswap.common.exception;

// Thrown when the user is logged in but is not allowed to perform this action.
public abstract class ForbiddenException extends RuntimeException {

    protected ForbiddenException(String message) {
        super(message);
    }
}
