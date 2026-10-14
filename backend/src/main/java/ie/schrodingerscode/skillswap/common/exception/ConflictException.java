package ie.schrodingerscode.skillswap.common.exception;

// Thrown when a request clashes with existing data, e.g. an email that is already registered.
// Features should extend this class instead of throwing it directly.
// The HTTP status is decided in GlobalExceptionHandler, not here.
public abstract class ConflictException extends RuntimeException {

    protected ConflictException(String message) {
        super(message);
    }
}
