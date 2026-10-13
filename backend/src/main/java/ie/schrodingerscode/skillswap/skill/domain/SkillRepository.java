package ie.schrodingerscode.skillswap.skill.domain;

import java.util.List;
import java.util.Optional;

public interface SkillRepository {
    List<Skill> findAll();
    Optional<Skill> findById(long id);
}