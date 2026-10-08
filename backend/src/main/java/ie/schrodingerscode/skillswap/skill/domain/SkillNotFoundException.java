package ie.schrodingerscode.skillswap.skill.domain;

import ie.schrodingerscode.skillswap.common.exception.NotFoundException;

public class SkillNotFoundException extends NotFoundException {
    public SkillNotFoundException(long skillId) {
        super("Skill " + skillId + " does not exist");
    }
}
