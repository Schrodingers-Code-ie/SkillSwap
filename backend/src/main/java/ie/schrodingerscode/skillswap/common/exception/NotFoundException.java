package ie.schrodingerscode.skillswap.common.exception;

// Thrown when the requested item does not exist.
public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }
}
