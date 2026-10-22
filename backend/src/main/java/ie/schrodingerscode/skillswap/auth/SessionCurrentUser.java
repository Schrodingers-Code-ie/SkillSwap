package ie.schrodingerscode.skillswap.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

/**
 * Reads the logged-in user from the session.
 * LoginController stores the user ID as the principal when someone logs in.
 */
@Component
public class SessionCurrentUser implements CurrentUser {

    @Override
    public long getId() {
        //spring security loads this from the session cookie before the controller runs
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //not logged in: no authentication at all, or spring's "anonymous" placeholder
        if (auth == null || auth instanceof AnonymousAuthenticationToken
                || !(auth.getPrincipal() instanceof Long id)) {
            throw new UnauthorizedException("Not logged in");
        }
        return id;
    }
}
