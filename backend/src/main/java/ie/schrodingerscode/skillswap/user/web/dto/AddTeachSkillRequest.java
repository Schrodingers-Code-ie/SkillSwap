package ie.schrodingerscode.skillswap.user.web.dto;

import ie.schrodingerscode.skillswap.user.domain.Proficiency;
import jakarta.validation.constraints.NotNull;

public record AddTeachSkillRequest(
        @NotNull(message = "skillId is required") Long skillId,
        Proficiency proficiency) {}