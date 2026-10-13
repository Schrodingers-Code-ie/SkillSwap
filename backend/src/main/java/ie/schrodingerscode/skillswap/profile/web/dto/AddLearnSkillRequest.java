package ie.schrodingerscode.skillswap.profile.web.dto;

import jakarta.validation.constraints.NotNull;

public record AddLearnSkillRequest(
        @NotNull(message = "skillId is required") Long skillId) {}