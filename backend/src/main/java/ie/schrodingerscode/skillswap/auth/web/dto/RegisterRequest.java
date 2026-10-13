package ie.schrodingerscode.skillswap.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
                @NotBlank String email,
                @NotBlank String password,
                @NotBlank String displayName,
                @NotNull Boolean confirmedAdult) {
}
