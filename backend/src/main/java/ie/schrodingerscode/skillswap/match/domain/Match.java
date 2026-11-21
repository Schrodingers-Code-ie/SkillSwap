package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;

/**
 * Holds a user who is a match for the current user, meaning they have at least one overlapping skill
 * in at least one direction (skillsTheyCanTeachMe or skillsICanTeachThem).
 *
 * @param matchPersonId            the other user's ID
 * @param matchPersonName          the other user's display name
 * @param matchPersonUsername      the other user's username
 * @param matchingScore            total number of overlapping skills in both directions, matches are sorted by this score
 * @param skillsTheyCanTeachMe     skills the other user can teach the current user
 * @param skillsICanTeachThem      skills the current user can teach the other user
 * @param unmatchedSkillsTheyTeach skills the other user teaches that the current user doesn't want to learn
 */
public record Match(long matchPersonId, String matchPersonName, String matchPersonUsername, int matchingScore,
        Set<String> skillsTheyCanTeachMe,
        Set<String> skillsICanTeachThem, Set<String> unmatchedSkillsTheyTeach) {
}
