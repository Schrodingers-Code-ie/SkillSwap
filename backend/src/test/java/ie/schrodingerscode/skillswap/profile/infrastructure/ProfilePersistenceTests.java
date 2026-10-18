package ie.schrodingerscode.skillswap.profile.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import ie.schrodingerscode.skillswap.profile.domain.TeachSkill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ProfilePersistenceTests {

    @Autowired ProfileRepository profiles;
    @Autowired SkillRepository skills;
    @Autowired JdbcTemplate jdbc;

    private long createUser() {
        String unique = UUID.randomUUID().toString().replace("-", "");
        return jdbc.queryForObject(
            "INSERT INTO users (email, username, password_hash) VALUES (?, ?, 'x') RETURNING id",
            Long.class, unique + "@test.com", "user_" + unique.substring(0, 8));
    }

    @Test
    void teachAndLearnSkillsSurviveReload() {
        long userId = createUser();
        Profile profile = new Profile(userId, "Test User", "Some bio");
        profile.addTeachSkill(1, Proficiency.ADVANCED);
        profile.addLearnSkill(12);
        profiles.save(profile);

        Profile reloaded = profiles.findById(userId).orElseThrow();

        assertThat(reloaded.getDisplayName()).isEqualTo("Test User");
        assertThat(reloaded.getBio()).isEqualTo("Some bio");
        assertThat(reloaded.getTeachSkills())
                .containsExactly(new TeachSkill(1, Proficiency.ADVANCED));
        assertThat(reloaded.getLearnSkillIds()).containsExactly(12L);
    }

    @Test
    void removedSkillsStayRemovedAfterReload() {
        long userId = createUser();
        Profile profile = new Profile(userId, "Test User", "bio");
        profile.addTeachSkill(1, Proficiency.BEGINNER);
        profile.addLearnSkill(12);
        profiles.save(profile);

        Profile loaded = profiles.findById(userId).orElseThrow();
        loaded.removeTeachSkill(1);
        loaded.removeLearnSkill(12);
        profiles.save(loaded);

        Profile reloaded = profiles.findById(userId).orElseThrow();
        assertThat(reloaded.getTeachSkills()).isEmpty();
        assertThat(reloaded.getLearnSkillIds()).isEmpty();
    }

    @Test
    void teachSkillIsStoredAsText() {
        long userId = createUser();
        Profile profile = new Profile(userId, "Test User", "bio");
        profile.addTeachSkill(1, Proficiency.INTERMEDIATE);
        profiles.save(profile);

        String stored = jdbc.queryForObject(
                "SELECT proficiency FROM user_teach_skills WHERE user_id = ? AND skill_id = 1",
                String.class, userId);

        assertThat(stored).isEqualTo("INTERMEDIATE");
    }

    @Test
    void unknownUserHasNoProfile() {
        assertThat(profiles.findById(999_999L)).isEmpty();
    }

    @Test
    void skillCatalogueComesFromTheDatabase() {
        assertThat(skills.findAll()).hasSize(25);
        assertThat(skills.findById(1)).get().extracting("name").isEqualTo("Guitar");
    }
}