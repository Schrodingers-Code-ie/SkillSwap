package ie.schrodingerscode.skillswap.skill.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface SkillJpaRepository extends JpaRepository<SkillEntity, Long> {}