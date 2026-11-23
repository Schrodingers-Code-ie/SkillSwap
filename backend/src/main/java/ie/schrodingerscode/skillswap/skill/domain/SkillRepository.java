package ie.schrodingerscode.skillswap.skill.domain;

import java.util.List;
import java.util.Optional;

/**
 * Read access to the skill catalogue. Nothing in the app writes to it, the skills come from the V2 migration.
 */
public interface SkillRepository {

    /**
     * The whole catalogue.
     *
     * @return every skill, sorted by id
     */
    List<Skill> findAll();

    /**
     * One skill by its id.
     *
     * @param id the skill to look for
     * @return the skill, or empty if there's none with this id
     */
    Optional<Skill> findById(long id);
}
