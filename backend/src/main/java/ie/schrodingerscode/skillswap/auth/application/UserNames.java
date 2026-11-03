package ie.schrodingerscode.skillswap.auth.application;

import java.util.Optional;

/**
 * Small interface other features can use to show a user's username,
 * without reaching into auth's repository or database table.
 * Connection uses it so requests and friends show "ann" instead of "User 5".
 */
public interface UserNames {

    Optional<String> usernameOf(long userId);
}
