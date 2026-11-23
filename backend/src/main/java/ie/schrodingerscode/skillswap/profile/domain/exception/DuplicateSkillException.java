package ie.schrodingerscode.skillswap.profile.domain.exception;

import ie.schrodingerscode.skillswap.common.exception.ConflictException;

/**
 * The skill is already on that list. Sent back as 409 Conflict.
 */
public class DuplicateSkillException extends ConflictException {

    /**
     * @param message says which skill and which list, for example "Skill 12 is already in the teach list"
     */
    public DuplicateSkillException(String message) {
        super(message);
    }
}
