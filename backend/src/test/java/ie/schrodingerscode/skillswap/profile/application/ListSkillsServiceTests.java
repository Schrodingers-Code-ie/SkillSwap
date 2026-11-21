package ie.schrodingerscode.skillswap.skill.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.skill.FakeSkillRepository;
import ie.schrodingerscode.skillswap.skill.domain.Skill;

class ListSkillsServiceTests {

    private final ListSkillsService service = new ListSkillsService(new FakeSkillRepository());

    @Test
    void listsTheWholeCatalogue() {
        assertThat(service.listAll())
                .extracting(Skill::name)
                .containsExactly("Guitar", "Music Production", "Java", "Python");
    }
}