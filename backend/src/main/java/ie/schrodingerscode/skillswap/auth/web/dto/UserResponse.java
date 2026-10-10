package ie.schrodingerscode.skillswap.auth.web.dto;

import ie.schrodingerscode.skillswap.auth.domain.User;

public record UserResponse(Long id, String email, String displayName) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail().value(), user.getDisplayName());
    }
}
