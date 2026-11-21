package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

/**
 * Holds the skills that overlap between the current user and one other user.
 *
 * @param userId               ID of the other user
 * @param username             username of the other user (may be null if that user was not found)
 * @param skillsTheyCanTeachMe skills the other user can teach the current user
 * @param skillsICanTeachThem  skills the current user can teach the other user
 */
public record SkillOverlap(long userId, String username, List<String> skillsTheyCanTeachMe,
        List<String> skillsICanTeachThem) {
}
