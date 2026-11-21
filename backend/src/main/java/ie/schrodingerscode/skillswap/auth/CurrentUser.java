package ie.schrodingerscode.skillswap.auth;

/**
 * Gives you the ID of the user making the current request.
 *
 * <p>Inject this into a controller or service and call {@link #getId()}. Feature code only depends on
 * this interface, not on Spring Security, so the way the user is read (now the session cookie, see
 * {@link SessionCurrentUser}) can change without touching any feature code.
 */
public interface CurrentUser {

    /**
     * Returns the logged-in user's ID.
     *
     * @return the ID from the {@code users} table
     * @throws ie.schrodingerscode.skillswap.common.exception.UnauthorizedException if nobody is logged in (401)
     */
    long getId();
}
