package ie.schrodingerscode.skillswap.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message); //passes the message to RuntimeException
    }
}
