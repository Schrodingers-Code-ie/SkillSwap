package ie.schrodingerscode.skillswap.profile.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ie.schrodingerscode.skillswap.profile.domain.exception.DuplicateSkillException;
import ie.schrodingerscode.skillswap.profile.domain.exception.InvalidProfileDetailsException;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProficiencyRequiredException;
import ie.schrodingerscode.skillswap.profile.domain.exception.SkillNotInListException;

public class Profile {

    public static final int MAX_NAME_LENGTH = 100; // matches profiles.full_name VARCHAR(100)
    public static final int MAX_BIO_LENGTH = 500;

    private final long userId;
    private String displayName;
    private String bio;
    private final List<TeachSkill> teachSkills = new ArrayList<>();
    private final Set<Long> learnSkillIds = new LinkedHashSet<>();

    public Profile(long userId, String displayName, String bio) {
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
    }

    //name is required and trimmed. a blank bio is stored as no bio
    public void updateDetails(String displayName, String bio) {
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidProfileDetailsException("Name is required");
        }
        String name = displayName.trim();
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidProfileDetailsException("Name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        String trimmedBio = bio == null || bio.isBlank() ? null : bio.trim();
        if (trimmedBio != null && trimmedBio.length() > MAX_BIO_LENGTH) {
            throw new InvalidProfileDetailsException("Bio must be at most " + MAX_BIO_LENGTH + " characters");
        }
        this.displayName = name;
        this.bio = trimmedBio;
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
