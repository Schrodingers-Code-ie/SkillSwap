package ie.schrodingerscode.skillswap.skill.domain;

import java.util.List;
import java.util.Optional;

/**
 * Read access to the skill catalogue. Nothing in the app writes to it, the skills come from the V2 migration.
 */
public interface SkillRepository {
    List<Skill> findAll();
    Optional<Skill> findById(long id);
}
