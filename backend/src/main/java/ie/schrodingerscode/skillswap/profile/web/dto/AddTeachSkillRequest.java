package ie.schrodingerscode.skillswap.profile.web.dto;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import jakarta.validation.constraints.NotNull;

public record AddTeachSkillRequest(
        @NotNull(message = "skillId is required") Long skillId,
        Proficiency proficiency) {}