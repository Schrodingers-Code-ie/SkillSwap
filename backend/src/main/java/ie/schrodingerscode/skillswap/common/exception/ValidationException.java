package ie.schrodingerscode.skillswap.common.exception;

// Thrown when the input breaks a business rule, e.g. skill level outside 1–5.
public abstract class ValidationException extends RuntimeException {

    protected ValidationException(String message) {
        super(message);
    }
}