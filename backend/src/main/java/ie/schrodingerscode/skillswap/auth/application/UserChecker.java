package ie.schrodingerscode.skillswap.auth.application;

/**
 * Small interface other features can use to ask "does this user exist?",
 * without reaching into auth's repository or database table.
 * Connection and block use it so a request about an unknown user gives 404 instead of a database error.
 */
public interface UserChecker {

    /**
     * Checks if a user with this ID exists.
     *
     * @param userId the ID to look for
     * @return true if there is a user with this ID
     */
    boolean exists(long userId);
}
