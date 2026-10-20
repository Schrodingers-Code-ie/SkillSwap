package ie.schrodingerscode.skillswap.auth.domain;

// Published by RegisterUser after a new user is saved. The profile listener uses it to create the profile.
public record UserRegistered(long userId, String displayName) {
}
