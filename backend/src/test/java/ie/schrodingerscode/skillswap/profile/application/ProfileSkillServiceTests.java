package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.skill.domain.SkillNotFoundException;
import ie.schrodingerscode.skillswap.skill.infrastructure.InMemorySkillRepository;
import ie.schrodingerscode.skillswap.profile.domain.exception.DuplicateSkillException;
import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProficiencyRequiredException;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;
import ie.schrodingerscode.skillswap.profile.domain.exception.SkillNotInListException;
import ie.schrodingerscode.skillswap.profile.infrastructure.InMemoryProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileSkillServiceTests {

    private static final long USER_ID = 100L;   // not one of the seeded users
    private static final long GUITAR = 1L;      // exists in the catalogue
    private static final long UNKNOWN_SKILL = 999L;

    private InMemoryProfileRepository profiles;
    private ProfileSkillService service;

    @BeforeEach
    void setUp() {
        profiles = new InMemoryProfileRepository();
        profiles.save(new Profile(USER_ID, "Test User", "Test bio"));
        // CurrentUser has one method, so a lambda works as a fake logged-in user
        service = new ProfileSkillService(profiles, new InMemorySkillRepository(), () -> USER_ID);
    }

    private Profile profile() {
        return profiles.findById(USER_ID).orElseThrow();
    }

    @Test
    void addsTeachSkillWithProficiency() {
        service.addTeachSkill(GUITAR, Proficiency.ADVANCED);

        assertThat(profile().getTeachSkills()).hasSize(1);
    }

    @Test
    void duplicateTeachSkillIsRejected() {
        service.addTeachSkill(GUITAR, Proficiency.BEGINNER);

        assertThatThrownBy(() -> service.addTeachSkill(GUITAR, Proficiency.ADVANCED))
                .isInstanceOf(DuplicateSkillException.class);
    }

    @Test
    void duplicateLearnSkillIsRejected() {
        service.addLearnSkill(GUITAR);

        assertThatThrownBy(() -> service.addLearnSkill(GUITAR))
                .isInstanceOf(DuplicateSkillException.class);
    }

    @Test
    void teachSkillWithoutProficiencyIsRejected() {
        assertThatThrownBy(() -> service.addTeachSkill(GUITAR, null))
                .isInstanceOf(ProficiencyRequiredException.class);
        assertThat(profile().getTeachSkills()).isEmpty();
    }

    @Test
    void learnSkillNeedsNoProficiency() {
        service.addLearnSkill(GUITAR);

        assertThat(profile().getLearnSkillIds()).containsExactly(GUITAR);
    }

    @Test
    void unknownSkillIsRejectedForTeachList() {
        assertThatThrownBy(() -> service.addTeachSkill(UNKNOWN_SKILL, Proficiency.BEGINNER))
                .isInstanceOf(SkillNotFoundException.class);
        assertThat(profile().getTeachSkills()).isEmpty();
    }

    @Test
    void unknownSkillIsRejectedForLearnList() {
        assertThatThrownBy(() -> service.addLearnSkill(UNKNOWN_SKILL))
                .isInstanceOf(SkillNotFoundException.class);
        assertThat(profile().getLearnSkillIds()).isEmpty();
    }

    @Test
    void unknownSkillIsReportedBeforeMissingProficiency() {
        // documents the order: 404 for the skill comes before the 400 for the level
        assertThatThrownBy(() -> service.addTeachSkill(UNKNOWN_SKILL, null))
                .isInstanceOf(SkillNotFoundException.class);
    }

    @Test
    void removesTeachSkill() {
        service.addTeachSkill(GUITAR, Proficiency.BEGINNER);

        service.removeTeachSkill(GUITAR);

        assertThat(profile().getTeachSkills()).isEmpty();
    }

    @Test
    void removesLearnSkill() {
        service.addLearnSkill(GUITAR);

        service.removeLearnSkill(GUITAR);

        assertThat(profile().getLearnSkillIds()).isEmpty();
    }

    @Test
    void removingSkillThatIsNotInTheListIsRejected() {
        assertThatThrownBy(() -> service.removeTeachSkill(GUITAR))
                .isInstanceOf(SkillNotInListException.class);
        assertThatThrownBy(() -> service.removeLearnSkill(GUITAR))
                .isInstanceOf(SkillNotInListException.class);
    }

    @Test
    void unknownUserHasNoProfile() {
        ProfileSkillService other =
                new ProfileSkillService(profiles, new InMemorySkillRepository(), () -> 555L);

        assertThatThrownBy(() -> other.addLearnSkill(GUITAR))
                .isInstanceOf(ProfileNotFoundException.class);
    }
}