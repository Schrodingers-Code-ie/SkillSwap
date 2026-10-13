package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;
import java.util.TreeSet;

public class MatchScorer {
    public Match score(SkillSwapUser me, SkillSwapUser other) {
        Set<String> skillsTheyCanTeachMe = new TreeSet<>(me.learn()); // Get ALL the skills *I* want to learn
        skillsTheyCanTeachMe.retainAll(other.teach()); // Leave only the skills from my "learn" which overlap with THEIR
                                                       // TEACH

        // Same for skills I can TEACH
        Set<String> skillsICanTeachThem = new TreeSet<>(me.teach()); // Get all skills I teach
        skillsICanTeachThem.retainAll(other.learn()); // Leave only the ones they want to learn

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

        return new Match(other.id(), other.name(), matchingScore, skillsTheyCanTeachMe, skillsICanTeachThem,
                unmatchedSkillsTheyTeach);
    }
}
