package ie.schrodingerscode.skillswap.profile.web.dto;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//bio is optional. null or blank removes it
public record UpdateProfileRequest(
        @NotBlank(message = "Name is required") @Size(max = Profile.MAX_NAME_LENGTH, message = "Name must be at most 100 characters") String displayName,

        @Size(max = Profile.MAX_BIO_LENGTH, message = "Bio must be at most 500 characters") String bio) {
}
