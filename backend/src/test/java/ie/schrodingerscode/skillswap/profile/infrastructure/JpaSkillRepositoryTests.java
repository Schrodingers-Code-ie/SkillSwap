package ie.schrodingerscode.skillswap.skill.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;

// Real Postgres in Docker, so this reads the 25 skills the V2 migration inserts
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class JpaSkillRepositoryTests {

    @Autowired
    SkillRepository skills;

    @Test
    void seededCatalogueIsLoadedInIdOrder() {
        assertThat(skills.findAll())
                .hasSize(25)
                .extracting(Skill::id)
                .isSorted();
        assertThat(skills.findAll().get(0)).isEqualTo(new Skill(1, "Guitar"));
    }

    @Test
    void findsSkillById() {
        assertThat(skills.findById(12)).contains(new Skill(12, "Python"));
    }

    @Test
    void unknownIdIsEmpty() {
        // ProfileSkillService turns this into a 404 when adding a skill
        assertThat(skills.findById(999_999L)).isEmpty();
    }
}