package ie.schrodingerscode.skillswap.skill.domain;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

/**
 * There's no skill with this id in the catalogue. Sent back as 404 Not Found.
 */
public class SkillNotFoundException extends NotFoundException {
    /**
     * @param skillId the id of the missing skill, shown in the message
     */
    public SkillNotFoundException(long skillId) {
        super("Skill " + skillId + " does not exist");
    }
}
