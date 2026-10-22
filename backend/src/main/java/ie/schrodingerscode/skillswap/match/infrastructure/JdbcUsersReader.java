package ie.schrodingerscode.skillswap.match.infrastructure;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.match.application.SkillSwapUsersReader;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

@Component
public class JdbcUsersReader implements SkillSwapUsersReader {
    private final JdbcClient jdbc;

    public JdbcUsersReader(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<SkillSwapUser> findById(long id) {
        // We need name, learn and teach sets, so it will be 3 small queries instead of one long one
        // Name:
        Optional<String> name = jdbc
                .sql("SELECT full_name FROM profiles WHERE user_id = :id")
                .param("id", id)
                .query(String.class)
                .optional();

        if (name.isEmpty())
            return Optional.empty(); // If the name is missing (i.e. there was no record with such id), we'll return empty right away and get an error

        String username = jdbc
                .sql("SELECT username FROM users WHERE id = :id")
                .param("id", id)
                .query(String.class)
                .single();

        // Teach skills (join with skills table to get the names, not the IDs of skills)
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

        // Learn skills (same process)
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

        // Assemble SkillSwapUser which we need to return with this function
        return Optional.of(new SkillSwapUser(id, name.get(), username, learn, teach)); // name.get() because of Optional
    }

    @Override
    public List<SkillSwapUser> findCandidatesFor(long meId) {
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

        // Everyone I already have a swap request with (pending or accepted), whichever way it was sent
        Set<Long> alreadyConnectedIds = new HashSet<>(jdbc
                .sql("""
                                SELECT receiver_id FROM swap_requests WHERE sender_id = :me
                                UNION
                                SELECT sender_id FROM swap_requests WHERE receiver_id = :me
                        """)
                .param("me", meId)
                .query(Long.class)
                .list());

        return skillsBasesIds.stream()
                .filter(id -> !alreadyConnectedIds.contains(id))
                .map(this::findById)
                .flatMap(Optional::stream)
                .toList();
    }
}
