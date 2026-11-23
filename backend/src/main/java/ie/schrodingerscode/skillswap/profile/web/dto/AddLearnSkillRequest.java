package ie.schrodingerscode.skillswap.profile.web.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Body of POST /api/me/learn-skills.
 *
 * @param skillId the skill to add, required (400 if missing)
 */
public record AddLearnSkillRequest(
        @NotNull(message = "skillId is required") Long skillId) {
}
