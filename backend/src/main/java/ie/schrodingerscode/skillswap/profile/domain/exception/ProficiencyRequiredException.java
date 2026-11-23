package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ValidationException;

/**
 * A teach skill was added without a level. Sent back as 400 Bad Request.
 */
public class ProficiencyRequiredException extends ValidationException {
    public ProficiencyRequiredException() {
        super("Proficiency is required for a teach skill");
    }
}
