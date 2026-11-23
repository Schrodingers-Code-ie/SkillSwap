package ie.schrodingerscode.skillswap.skill;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;

import java.util.List;
import java.util.Optional;

/**
 * A small fixed skill catalogue for unit tests. The ids match the real seed data in V2,
 * so tests can use the same ids as the app (1 Guitar, 5 Music Production, 11 Java, 12 Python).
 */

public class FakeSkillRepository implements SkillRepository {

    private final List<Skill> skills = List.of(
            new Skill(1, "Guitar"),
            new Skill(5, "Music Production"),
            new Skill(11, "Java"),
            new Skill(12, "Python"));

    @Override
    public List<Skill> findAll() {
        return skills;
    }

    @Override
    public Optional<Skill> findById(long id) {
        return skills.stream().filter(s -> s.id() == id).findFirst();
    }
}
