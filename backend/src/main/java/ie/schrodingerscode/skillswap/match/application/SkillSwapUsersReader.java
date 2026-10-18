package ie.schrodingerscode.skillswap.match.application;

import java.util.List;
import java.util.Optional;

import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

// An interface used for the class that reads in users straight from the database
public interface SkillSwapUsersReader {
    Optional<SkillSwapUser> findById(long id);

    List<SkillSwapUser> findCandidatesFor(long meId);
}
