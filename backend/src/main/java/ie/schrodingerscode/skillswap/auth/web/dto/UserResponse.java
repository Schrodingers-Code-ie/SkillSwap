package ie.schrodingerscode.skillswap.auth.web.dto;

import ie.schrodingerscode.skillswap.auth.domain.User;

/**
 * The user as sent back to the frontend. It never includes the password hash.
 *
 * @param id the user's ID
 * @param email the normalised email
 * @param username the username
 */
public record UserResponse(Long id, String email, String username) {

    /**
     * Builds the response from a domain user.
     *
     * @param user the user to send back
     * @return the response
     */
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail().value(), user.getUsername());
    }
}
