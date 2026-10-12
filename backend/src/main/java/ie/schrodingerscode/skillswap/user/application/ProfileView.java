package ie.schrodingerscode.skillswap.user.application;

import ie.schrodingerscode.skillswap.user.domain.Proficiency;
import java.util.List;

public record ProfileView(
        long userId,
        String displayName,
        String bio,
        List<TeachSkillView> teachSkills,
        List<SkillView> learnSkills) {

    public record TeachSkillView(long skillId, String name, Proficiency proficiency) {}

    public record SkillView(long skillId, String name) {}
}