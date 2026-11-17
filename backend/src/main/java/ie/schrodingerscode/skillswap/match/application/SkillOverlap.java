package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

// skills I can swap with one person
public record SkillOverlap(long userId, String username, List<String> skillsTheyCanTeachMe,
        List<String> skillsICanTeachThem) {
}
