package ie.schrodingerscode.skillswap.profile.web.dto;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import jakarta.validation.constraints.NotNull;

/**
 * Body of POST /api/me/teach-skills.
 * proficiency has no @NotNull on purpose: Profile checks it, so the error is the same wherever it comes from.
 */
public record AddTeachSkillRequest(
        @NotNull(message = "skillId is required") Long skillId,
        Proficiency proficiency) {
}
