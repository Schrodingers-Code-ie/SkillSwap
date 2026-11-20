package ie.schrodingerscode.skillswap.match.application;

import java.util.List;
import java.util.Optional;

import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

/**
 * Source of SkillSwapUsers for the match feature. Implemented by JdbcUsersReader,
 * which reads users from Postgres.
 */
public interface SkillSwapUsersReader {

    /**
     * Finds one user by ID.
     *
     * @param id the user's ID
     * @return   the user, or an empty Optional if the user doesn't exist
     */
    Optional<SkillSwapUser> findById(long id);

    /**
     * Finds the users who could be a match for the given user: users with at least one overlapping skill,
     * excluding users who have a swap request with, or a block against, the given user.
     *
     * @param meId ID of the current user
     * @return     the candidate users (empty list if there are none)
     */
    List<SkillSwapUser> findCandidatesFor(long meId);
}
