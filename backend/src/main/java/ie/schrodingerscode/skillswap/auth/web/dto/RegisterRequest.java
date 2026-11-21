package ie.schrodingerscode.skillswap.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * JSON body for {@code POST /api/auth/register}.
 *
 * <p>The annotations only check that each field is sent. The real rules (lengths, formats) are in
 * {@code RegisterUser} and the domain.
 *
 * @param email the email as typed
 * @param username the username, 3 to 20 letters, numbers or underscores
 * @param password the plain password, 8 to 72 characters
 * @param displayName the full name for the profile
 * @param confirmedAdult true if the user is 18 or older, the date of birth itself is not sent
 */
public record RegisterRequest(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String displayName,
        @NotNull Boolean confirmedAdult) {
}
