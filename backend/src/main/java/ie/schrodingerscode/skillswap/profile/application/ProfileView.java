package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import java.util.List;

public record ProfileView(
        long userId,
        String displayName,
        String bio,
        List<TeachSkillView> teachSkills,
        List<SkillView> learnSkills) {

    public record TeachSkillView(long skillId, String name, Proficiency proficiency) {
    }

    public record SkillView(long skillId, String name) {
    }
}
