package ie.schrodingerscode.skillswap.match.application;

import java.util.List;
import java.util.Optional;

import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

// An interface used for the class that reads in users straight from the database
public interface SkillSwapUsersReader {
    /** Finds one user with passed id - kpet with Optional just in case (even though the current user is checked for existence when logging in) */
    Optional<SkillSwapUser> findById(long id);

    /** Users who could swap with meId, excluding meId, blocked users and existing connections */
    List<SkillSwapUser> findCandidatesFor(long meId);
}
