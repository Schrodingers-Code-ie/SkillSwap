package ie.schrodingerscode.skillswap.auth;

/**
 * Gives you the ID of the user making the current request.
 *
 * Inject this into a controller or service and call getId().
 * Feature code only depends on this interface, so when real login
 * replaces the header stub, no feature code has to change.
 */
public interface CurrentUser {

    /**
     * Returns the current user's ID.
     * Throws UnauthorizedException (401) if nobody is logged in.
     */
    long getId();
}
