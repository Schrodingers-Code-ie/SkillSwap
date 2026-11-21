package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import java.util.List;

/**
 * What the profile page needs, sent back as JSON as it is.
 * Skill names are included, so the frontend doesn't need a second call to match ids to names.
 */
public record ProfileView(
        long userId,
        String displayName,
        String bio,
        List<TeachSkillView> teachSkills,
        List<SkillView> learnSkills) {

    /**
     * A skill on the teach list, with the user's level.
     */
    public record TeachSkillView(long skillId, String name, Proficiency proficiency) {
    }

    /**
     * A skill on the learn list.
     */
    public record SkillView(long skillId, String name) {
    }
}
