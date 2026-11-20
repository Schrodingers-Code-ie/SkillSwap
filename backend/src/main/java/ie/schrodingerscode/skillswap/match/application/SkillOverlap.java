package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

/**
 * Skills I can swap with one person.
 *
 * @param userId their id
 * @param username their username
 * @param skillsTheyCanTeachMe skills they teach that I want to learn
 * @param skillsICanTeachThem skills I teach that they want to learn
 */
public record SkillOverlap(long userId, String username, List<String> skillsTheyCanTeachMe,
        List<String> skillsICanTeachThem) {
}
