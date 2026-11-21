package ie.schrodingerscode.skillswap.skill.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data writes the implementation, no custom queries needed.
 */
interface SkillJpaRepository extends JpaRepository<SkillEntity, Long> {
}
