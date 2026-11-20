package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

/**
 * Connection uses this to get the skills for the friend cards.
 */
public interface SharedSkills {

    /**
     * Gets the skills I can swap with each of these people.
     *
     * @param meId the logged in user
     * @param otherIds the friends to check
     * @return one SkillOverlap per friend
     */
    List<SkillOverlap> between(long meId, List<Long> otherIds);
}
