package ie.schrodingerscode.skillswap.match.application;

import java.util.List;

// connection uses this to get the skills for the friend cards
public interface SharedSkills {

    List<SkillOverlap> between(long meId, List<Long> otherIds);
}
