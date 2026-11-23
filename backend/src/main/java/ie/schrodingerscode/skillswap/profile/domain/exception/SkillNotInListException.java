package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * The skill is not on that list. Sent back as 404 Not Found.
 */
public class SkillNotInListException extends NotFoundException {
    public SkillNotInListException(String message) {
        super(message);
    }
}
