package ie.schrodingerscode.skillswap.auth.domain;

import java.time.Instant;

import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidDisplayNameException;

public class User {
    private final Long id;
    private final Email email;
    private final String passwordHash;
    private final String displayName;
    private final Instant createdAt;

    private User(Long id, Email email, String passwordHash, String displayName, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public static User register(Email email, String passwordHash, String displayName, boolean confirmedAdult) {
        if (!confirmedAdult) {
            throw new AgeNotConfirmedException("You must confirm you are 18 or older");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidDisplayNameException("Display name is required");
        }
        displayName = displayName.trim();
        if (displayName.length() > 100) {
            throw new InvalidDisplayNameException("Display name must be at most 100 characters");
        }
        return new User(null, email, passwordHash, displayName, Instant.now());
    }

    public User withId(long id) {
        return new User(id, email, passwordHash, displayName, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Email getEmail() {
        return email;
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
