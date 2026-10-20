package ie.schrodingerscode.skillswap.auth;

import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * OLD stub: reads the user ID from the X-User-Id request header.
 * No longer a @Component, the app uses SessionCurrentUser now.
 * Only kept because some controller tests still @Import it.
 * TODO: delete once SCRUM-71 and SCRUM-72 are merged.
 */
public class HeaderCurrentUser implements CurrentUser {

    static final String HEADER = "X-User-Id";

    //spring injects a proxy that always points at the request being handled right now
    private final HttpServletRequest request;

    public HeaderCurrentUser(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public long getId() {
        String value = request.getHeader(HEADER);
        if (value == null || value.isBlank()) {
            throw new UnauthorizedException("Missing " + HEADER + " header");
        }
        try {
            long id = Long.parseLong(value.trim());
            if (id <= 0) {
                throw new UnauthorizedException("Invalid " + HEADER + " header");
            }
            return id;
        } catch (NumberFormatException ex) { //"abc" is not a number
            throw new UnauthorizedException("Invalid " + HEADER + " header");
        }
    }
}
