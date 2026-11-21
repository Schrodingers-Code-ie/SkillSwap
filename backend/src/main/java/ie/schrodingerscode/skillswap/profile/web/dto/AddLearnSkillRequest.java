package ie.schrodingerscode.skillswap.profile.web.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Body of POST /api/me/learn-skills.
 */
public record AddLearnSkillRequest(
        @NotNull(message = "skillId is required") Long skillId) {
}
