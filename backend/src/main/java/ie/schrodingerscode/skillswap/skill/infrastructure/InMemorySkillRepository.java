package ie.schrodingerscode.skillswap.skill.infrastructure;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemorySkillRepository implements SkillRepository {
private final List<Skill> skills = List.of(
        new Skill(1, "Guitar"),
        new Skill(2, "Piano"),
        new Skill(3, "Singing"),
        new Skill(4, "Drums"),
        new Skill(5, "Music Production"),
        new Skill(6, "Spanish"),
        new Skill(7, "French"),
        new Skill(8, "Irish"),
        new Skill(9, "German"),
        new Skill(10, "Japanese"),
        new Skill(11, "Java"),
        new Skill(12, "Python"),
        new Skill(13, "JavaScript"),
        new Skill(14, "SQL"),
        new Skill(15, "Web Design"),
        new Skill(16, "Baking"),
        new Skill(17, "Meal Prep"),
        new Skill(18, "Italian Cuisine"),
        new Skill(19, "Vegetarian Cooking"),
        new Skill(20, "Cocktail Making"),
        new Skill(21, "Yoga"),
        new Skill(22, "Running"),
        new Skill(23, "Weight Training"),
        new Skill(24, "Climbing"),
        new Skill(25, "Swimming")
);

    @Override
    public List<Skill> findAll() {
        return skills;
    }

    @Override
    public Optional<Skill> findById(long id) {
        return skills.stream().filter(s -> s.id() == id).findFirst();
    }

}