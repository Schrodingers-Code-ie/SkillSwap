package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;
import java.util.TreeSet;

public class MatchScorer {
    public Match score(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skillsTheyCanTeachMe = skillsTheyCanTeachMe(me, other);
        Set<String> skillsICanTeachThem = skillsICanTeachThem(me, other);

        // Opposite for unmatching skills
        Set<String> unmatchedSkillsTheyTeach = new TreeSet<>(other.teach()); // All skills they teach
        unmatchedSkillsTheyTeach.removeAll(skillsTheyCanTeachMe); // And remove the ones I want to learn, so that we
                                                                  // only have the ones I DON'T want to learn

        if (skillsICanTeachThem.isEmpty() && skillsTheyCanTeachMe.isEmpty()) // If BOTH sections are empty, we don't
                                                                             // show this other person in the
                                                                             // suggestions at all
            return null;

        // Calculating the matching score based on what we now know about overlapping
        // skills in two sections
        // Easiest scoring system: each overlapping skill in any section = 1 matching
        // point
        int matchingScore = skillsICanTeachThem.size() + skillsTheyCanTeachMe.size();

        return new Match(other.id(), other.name(), other.username(), matchingScore, skillsTheyCanTeachMe,
                skillsICanTeachThem,
                unmatchedSkillsTheyTeach);
    }

    // what they can teach me
    public static Set<String> skillsTheyCanTeachMe(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skills = new TreeSet<>(me.learn());
        skills.retainAll(other.teach());
        return skills;
    }

    // what I can teach them
    public static Set<String> skillsICanTeachThem(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skills = new TreeSet<>(me.teach());
        skills.retainAll(other.learn());
        return skills;
    }
}
