package ie.schrodingerscode.skillswap.match.infrastructure;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.match.application.SkillSwapUsersReader;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

/**
 * Reads users from Postgres using SQL queries through JdbcClient.
 * Implements SkillSwapUsersReader.
 */
@Component
public class JdbcUsersReader implements SkillSwapUsersReader {
    private final JdbcClient jdbc;

    /**
     * Creates the reader.
     *
     * @param jdbc client used to run SQL queries against Postgres
     */
    public JdbcUsersReader(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Loads one user from the database. A SkillSwapUser needs more than an ID, so this runs four
     * separate queries instead of one big one: display name (profiles table), username (users table),
     * skills the user teaches and skills the user wants to learn (both joined with the skills table to get
     * skill names instead of IDs).
     *
     * @param id the user's ID
     * @return   the user, or an empty Optional if there is no profile for this ID
     */
    @Override
    public Optional<SkillSwapUser> findById(long id) {
        Optional<String> name = jdbc
                .sql("SELECT full_name FROM profiles WHERE user_id = :id") // name is stored in profiles table
                .param("id", id)
                .query(String.class)
                .optional();

        if (name.isEmpty())
            return Optional.empty(); // No profile with this id, so there is no user to return

        String username = jdbc
                .sql("SELECT username FROM users WHERE id = :id") // username is stored in users table
                .param("id", id)
                .query(String.class)
                .single();

        // Teach skills (join with skills table to get the names of the skills, not their IDs):
        Set<String> teach = new HashSet<>(jdbc
                .sql("""
                        SELECT s.name
                        FROM user_teach_skills uts
                        JOIN skills s ON s.id = uts.skill_id
                        WHERE uts.user_id = :id
                        """)
                .param("id", id)
                .query(String.class)
                .list());

        // Learn skills (same process):
        Set<String> learn = new HashSet<>(jdbc
                .sql("""
                        SELECT s.name
                        FROM user_learn_skills uls
                        JOIN skills s ON s.id = uls.skill_id
                        WHERE uls.user_id = :id
                        """)
                .param("id", id)
                .query(String.class)
                .list());

        // Assemble SkillSwapUser
        return Optional.of(new SkillSwapUser(id, name.get(), username, learn, teach)); // name.get() because of Optional
    }

    /**
     * Gets the candidate users for the current user. A candidate is a user who has at least one overlapping
     * skill with the current user, except users who:
     * 1) have a swap request with the current user in either direction (pending or accepted)
     * 2) blocked the current user or were blocked by the current user
     *
     * @param meId ID of the current user
     * @return     the candidate users (empty list if there are none)
     */
    @Override
    public List<SkillSwapUser> findCandidatesFor(long meId) {
        // IDs of all users who overlap with the current user's skills (in either direction)
        List<Long> skillsBasesIds = jdbc
                .sql("""
                        SELECT t.user_id
                        FROM user_teach_skills t
                        JOIN user_learn_skills myLearn
                        ON myLearn.skill_id = t.skill_id AND myLearn.user_id = :me
                        WHERE t.user_id <> :me

                        UNION

                        SELECT l.user_id
                        FROM user_learn_skills l
                        JOIN user_teach_skills myTeach
                        ON myTeach.skill_id = l.skill_id AND myTeach.user_id = :me
                        WHERE l.user_id <> :me
                        """)
                .param("me", meId)
                .query(Long.class)
                .list();

        // IDs that must be excluded: swap requests and blocks, in both directions
        Set<Long> excludedIds = new HashSet<>(jdbc
                .sql("""
                                SELECT receiver_id FROM swap_requests WHERE sender_id = :me
                                UNION
                                SELECT sender_id FROM swap_requests WHERE receiver_id = :me
                                UNION
                                SELECT blocked_id FROM blocks WHERE blocker_id = :me
                                UNION
                                SELECT blocker_id FROM blocks WHERE blocked_id = :me
                        """)
                .param("me", meId)
                .query(Long.class)
                .list());

        // Combining two results
        return skillsBasesIds.stream()
                .filter(id -> !excludedIds.contains(id)) // Keep only those IDs which are not in the excludedIds
                .map(this::findById) // On each good ID, we call findById to get the actual user (means extra 4 SQL queries for each ID)
                .flatMap(Optional::stream)
                .toList();
    }
}
