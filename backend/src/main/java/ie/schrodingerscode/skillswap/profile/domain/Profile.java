package ie.schrodingerscode.skillswap.profile.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ie.schrodingerscode.skillswap.profile.domain.exception.DuplicateSkillException;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProficiencyRequiredException;
import ie.schrodingerscode.skillswap.profile.domain.exception.SkillNotInListException;

public class Profile {

    private final long userId;
    private final String displayName;
    private final String bio;
    private final List<TeachSkill> teachSkills = new ArrayList<>();
    private final Set<Long> learnSkillIds = new LinkedHashSet<>();

    public Profile(long userId, String displayName, String bio) {
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
    }

    public void addTeachSkill(long skillId, Proficiency proficiency) {
        if (proficiency == null || proficiency == Proficiency.NONE) {
            throw new ProficiencyRequiredException();
        }
        if (hasTeachSkill(skillId)) {
            throw new DuplicateSkillException("Skill " + skillId + " is already in the teach list");
        }
        teachSkills.add(new TeachSkill(skillId, proficiency));
    }

    public void addLearnSkill(long skillId) {
        if (!learnSkillIds.add(skillId)) {
            throw new DuplicateSkillException("Skill " + skillId + " is already in the learn list");
        }
    }

    public void removeTeachSkill(long skillId) {
        boolean removed = teachSkills.removeIf(t -> t.skillId() == skillId);
        if (!removed) {
            throw new SkillNotInListException("Skill " + skillId + " is not in the teach list");
        }
    }

    public void removeLearnSkill(long skillId) {
        if (!learnSkillIds.remove(skillId)) {
            throw new SkillNotInListException("Skill " + skillId + " is not in the learn list");
        }
    }

    private boolean hasTeachSkill(long skillId) {
        return teachSkills.stream().anyMatch(t -> t.skillId() == skillId);
    }

    public long getUserId() {
        return userId;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getBio() {
        return bio;
    }

    // Copies, so callers can't bypass the rules by editing the collections.
    public List<TeachSkill> getTeachSkills() {
        return List.copyOf(teachSkills);
    }
    public Set<Long> getLearnSkillIds() {
        return Set.copyOf(learnSkillIds);
    }
}
