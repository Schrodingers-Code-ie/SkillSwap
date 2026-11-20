package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;
import java.util.TreeSet;

/**
 * Decides whether two users are a match and scores the match. Two users match if they overlap in at least
 * one direction: the other user teaches something the current user wants to learn, or wants to learn something
 * the current user teaches. The matching score is the total number of overlapping skills in both directions,
 * and is used to order matches.
 */
public class MatchScorer {
    /**
     * Checks whether two users are a match. If so, calculates the matching score and returns a Match.
     *
     * @param me    the current user
     * @param other the other user
     * @return      a Match or null if the users have no overlapping skills
     */
    public Match score(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skillsTheyCanTeachMe = skillsTheyCanTeachMe(me, other);
        Set<String> skillsICanTeachThem = skillsICanTeachThem(me, other);

        Set<String> unmatchedSkillsTheyTeach = new TreeSet<>(other.teach());
        unmatchedSkillsTheyTeach.removeAll(skillsTheyCanTeachMe);

        // Both directions empty, therefore "other" is not a match to "me", don't save "other" to matches
        if (skillsICanTeachThem.isEmpty() && skillsTheyCanTeachMe.isEmpty())
            return null;

        int matchingScore = skillsICanTeachThem.size() + skillsTheyCanTeachMe.size();

        return new Match(other.id(), other.name(), other.username(), matchingScore, skillsTheyCanTeachMe,
                skillsICanTeachThem,
                unmatchedSkillsTheyTeach);
    }

    /**
     * Returns the skills the other user can teach the current user.
     *
     * @param me    the current user
     * @param other the other user
     * @return      a new set of skill names sorted alphabetically (empty if there is no overlap)
     */
    public static Set<String> skillsTheyCanTeachMe(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skills = new TreeSet<>(me.learn()); // TreeSet keeps the results sorted alphabetically
        skills.retainAll(other.teach()); // Keep only those skills that the other person teaches
        return skills;
    }

    /**
     * Returns the skills the current user can teach the other user.
     *
     * @param me    the current user
     * @param other the other user
     * @return      a new set of skill names sorted alphabetically (empty if there is no overlap)
     */
    public static Set<String> skillsICanTeachThem(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skills = new TreeSet<>(me.teach());
        skills.retainAll(other.learn());
        return skills;
    }
}
