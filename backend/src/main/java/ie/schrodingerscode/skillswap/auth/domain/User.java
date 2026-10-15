package ie.schrodingerscode.skillswap.auth.domain;

import java.time.Instant;

import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidDisplayNameException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidUsernameException;

public class User {
    private final Long id;
    private final Email email;
    private final String username;
    private final String passwordHash;
    private final String displayName;
    private final Instant createdAt;

    private User(Long id, Email email, String username, String passwordHash, String displayName, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public static User register(Email email, String username, String passwordHash, String displayName,
            boolean confirmedAdult) {
        if (!confirmedAdult) {
            throw new AgeNotConfirmedException("You must confirm you are 18 or older");
        }
        username = checkUsername(username);
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidDisplayNameException("Display name is required");
        }
        displayName = displayName.trim();
        if (displayName.length() > 100) {
            throw new InvalidDisplayNameException("Display name must be at most 100 characters");
        }
        return new User(null, email, username, passwordHash, displayName, Instant.now());
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

    public User withId(long id) {
        return new User(id, email, username, passwordHash, displayName, createdAt);
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

    public String getDisplayName() {
        return displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
