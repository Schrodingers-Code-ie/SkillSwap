package ie.schrodingerscode.skillswap.auth.application;

/**
 * Small interface other features can use to ask "does this user exist?",
 * without reaching into auth's repository or database table.
 * Connection uses it so a swap request to an unknown user gives 404 instead of a database error.
 */
public interface UserChecker {

    boolean exists(long userId);
}
