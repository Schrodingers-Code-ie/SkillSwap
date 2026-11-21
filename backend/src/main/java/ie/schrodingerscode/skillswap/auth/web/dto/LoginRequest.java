package ie.schrodingerscode.skillswap.auth.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * JSON body for {@code POST /api/auth/login}.
 *
 * @param email the email as typed
 * @param password the plain password
 */
public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password) {
}
