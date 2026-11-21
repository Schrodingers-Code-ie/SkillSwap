package ie.schrodingerscode.skillswap.auth.domain;

/**
 * Event published by {@code RegisterUser} after a new user is saved.
 *
 * <p>The profile feature listens for it and creates the user's profile. This way auth does not have
 * to know about profiles, it only says "a user was registered".
 *
 * @param userId the new user's ID
 * @param displayName the name typed on the sign up form, already trimmed
 */
public record UserRegistered(long userId, String displayName) {
}
