package ie.schrodingerscode.skillswap.profile.web.dto;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of PUT /api/me/profile. bio is optional, null or blank removes it.
 * These checks give a quick 400, Profile.updateDetails checks the same rules again on the trimmed values.
 */
public record UpdateProfileRequest(
        @NotBlank(message = "Name is required") @Size(max = Profile.MAX_NAME_LENGTH, message = "Name must be at most 100 characters") String displayName,

        @Size(max = Profile.MAX_BIO_LENGTH, message = "Bio must be at most 500 characters") String bio) {
}
