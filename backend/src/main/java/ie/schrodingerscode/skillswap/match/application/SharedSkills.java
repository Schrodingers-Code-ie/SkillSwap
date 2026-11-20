package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

/**
 * Provides the skills two users have in common, e.g. to fill the cards on the Friends page.
 */
public interface SharedSkills {

    /**
     * Returns the skill overlap between the current user and each of the given users.
     *
     * @param meId     ID of the current user
     * @param otherIds IDs of the other users to compare with
     * @return         one SkillOverlap per ID in otherIds, in the same order
     */
    List<SkillOverlap> between(long meId, List<Long> otherIds);
}
