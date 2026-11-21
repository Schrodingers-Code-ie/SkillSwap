package ie.schrodingerscode.skillswap.auth.domain;

import java.time.Instant;

import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidUsernameException;

/**
 * A SkillSwap account: email, username and password hash.
 *
 * <p>This is the domain version of a user, with no JPA annotations. New users can only be made with
 * {@link #register}, which checks the rules, so a {@code User} always has a valid username and a
 * confirmed age. The profile (display name, bio, skills) lives in the profile feature, not here.
 */
public class User {
    private final Long id;
    private final Email email;
    private final String username;
    private final String passwordHash;
    private final Instant createdAt;

    private User(Long id, Email email, String username, String passwordHash, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    /**
     * Creates a new user that is not saved yet, so the ID is still null.
     *
     * @param email the already checked email
     * @param username the username, trimmed before it is checked
     * @param passwordHash the bcrypt hash, never the plain password
     * @param confirmedAdult whether the user ticked that they are 18 or older
     * @return the new user with {@code createdAt} set to now
     * @throws AgeNotConfirmedException if {@code confirmedAdult} is false
     * @throws InvalidUsernameException if the username is empty, not 3 to 20 characters or has other
     *         characters than letters, numbers and underscore
     */
    public static User register(Email email, String username, String passwordHash, boolean confirmedAdult) {
        if (!confirmedAdult) {
            throw new AgeNotConfirmedException("You must confirm you are 18 or older");
        }
        username = checkUsername(username);

        return new User(null, email, username, passwordHash, Instant.now());
    }

    private static String checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException("Username is required");
        }
        username = username.trim();
        if (username.length() < 3 || username.length() > 20) {
            throw new InvalidUsernameException("Username must be 3 to 20 characters");
        }
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            throw new InvalidUsernameException("Username can only contain letters, numbers and underscore");
        }
        return username;
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Rebuilds a user that is already in the database. It skips the sign-up checks because the data
     * was checked when the user registered. Only the repository should call this.
     *
     * @param id the database ID
     * @param email the stored email
     * @param username the stored username
     * @param passwordHash the stored bcrypt hash
     * @param createdAt when the account was created
     * @return the user
     */
    public static User restore(Long id, Email email, String username, String passwordHash, Instant createdAt) {
        return new User(id, email, username, passwordHash, createdAt);
    }
}
