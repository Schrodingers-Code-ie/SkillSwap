package ie.schrodingerscode.skillswap.profile.web.dto;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import jakarta.validation.constraints.NotNull;

/**
 * Body of POST /api/me/teach-skills.
 *
 * @param skillId the skill to add, required (400 if missing)
 * @param proficiency the user's level. No @NotNull on purpose: Profile checks it,
 *        so the error is the same wherever it comes from
 */
public record AddTeachSkillRequest(
        @NotNull(message = "skillId is required") Long skillId,
        Proficiency proficiency) {
}
