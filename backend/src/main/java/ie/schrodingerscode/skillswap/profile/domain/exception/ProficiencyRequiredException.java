package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

public class ProficiencyRequiredException extends ValidationException {
    public ProficiencyRequiredException() {
        super("Proficiency is required for a teach skill");
    }
}