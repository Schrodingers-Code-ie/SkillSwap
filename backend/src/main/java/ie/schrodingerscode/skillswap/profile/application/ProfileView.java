package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import java.util.List;

/**
 * What the profile page needs, sent back as JSON as it is.
 * Skill names are included, so the frontend doesn't need a second call to match ids to names.
 *
 * @param userId whose profile this is
 * @param displayName the name shown on the page
 * @param bio the bio, or null if there isn't one
 * @param teachSkills the skills they can teach, with names and levels
 * @param learnSkills the skills they want to learn, with names
 */
public record ProfileView(
        long userId,
        String displayName,
        String bio,
        List<TeachSkillView> teachSkills,
        List<SkillView> learnSkills) {

    /**
     * A skill on the teach list, with the user's level.
     *
     * @param skillId the skill's id
     * @param name the skill's name
     * @param proficiency the user's level
     */
    public record TeachSkillView(long skillId, String name, Proficiency proficiency) {
    }

    /**
     * A skill on the learn list.
     *
     * @param skillId the skill's id
     * @param name the skill's name
     */
    public record SkillView(long skillId, String name) {
    }
}
