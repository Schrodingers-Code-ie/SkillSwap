package ie.schrodingerscode.skillswap.connection.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * You can't send a request to yourself (400).
 */
public class SelfRequestException extends ValidationException {
    public SelfRequestException() {
        super("You can't send a swap request to yourself");
    }
}
