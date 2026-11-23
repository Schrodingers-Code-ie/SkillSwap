package ie.schrodingerscode.skillswap.skill.infrastructure;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Reads the skills table through JPA. Package-private, so the rest of the app only sees SkillRepository.
 */
@Repository
class JpaSkillRepository implements SkillRepository {

    private final SkillJpaRepository jpa;

    JpaSkillRepository(SkillJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * Loads every row of the skills table.
     *
     * @return all skills, sorted by id
     */
    @Override
    public List<Skill> findAll() {
        return jpa.findAll(Sort.by("id")).stream().map(SkillEntity::toDomain).toList();
    }

    /**
     * Loads one skill by its id.
     *
     * @param id the skill to look for
     * @return the skill, or empty if there's no row with this id
     */
    @Override
    public Optional<Skill> findById(long id) {
        return jpa.findById(id).map(SkillEntity::toDomain);
    }
}
