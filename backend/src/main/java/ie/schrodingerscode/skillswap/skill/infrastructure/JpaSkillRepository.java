package ie.schrodingerscode.skillswap.skill.infrastructure;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaSkillRepository implements SkillRepository {

    private final SkillJpaRepository jpa;

    JpaSkillRepository(SkillJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Skill> findAll() {
        return jpa.findAll(Sort.by("id")).stream().map(SkillEntity::toDomain).toList();
    }

    @Override
    public Optional<Skill> findById(long id) {
        return jpa.findById(id).map(SkillEntity::toDomain);
    }
}