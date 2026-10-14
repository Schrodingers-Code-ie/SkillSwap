package ie.schrodingerscode.skillswap.profile.domain;

import ie.schrodingerscode.skillswap.profile.domain.exception.DuplicateSkillException;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProficiencyRequiredException;
import ie.schrodingerscode.skillswap.profile.domain.exception.SkillNotInListException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTests {

    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile(1L, "Alice", "Loves music");
    }

    @Test
    void addsTeachSkillWithProficiency() {
        profile.addTeachSkill(1L, Proficiency.ADVANCED);

        assertThat(profile.getTeachSkills())
                .containsExactly(new TeachSkill(1L, Proficiency.ADVANCED));
    }

    @Test
    void teachSkillWithoutProficiencyIsRejected() {
        assertThatThrownBy(() -> profile.addTeachSkill(1L, null))
                .isInstanceOf(ProficiencyRequiredException.class);
    }

    @Test
    void sameTeachSkillTwiceIsRejected() {
        profile.addTeachSkill(1L, Proficiency.BEGINNER);

        assertThatThrownBy(() -> profile.addTeachSkill(1L, Proficiency.ADVANCED))
                .isInstanceOf(DuplicateSkillException.class);
    }

    @Test
    void learnSkillNeedsNoProficiency() {
        profile.addLearnSkill(2L);

        assertThat(profile.getLearnSkillIds()).containsExactly(2L);
    }

    @Test
    void sameLearnSkillTwiceIsRejected() {
        profile.addLearnSkill(2L);

        assertThatThrownBy(() -> profile.addLearnSkill(2L))
                .isInstanceOf(DuplicateSkillException.class);
    }

    @Test
    void sameSkillCanBeInBothLists() {
        profile.addTeachSkill(3L, Proficiency.INTERMEDIATE);
        profile.addLearnSkill(3L);

        assertThat(profile.getTeachSkills()).hasSize(1);
        assertThat(profile.getLearnSkillIds()).containsExactly(3L);
    }

    @Test
    void removesTeachSkill() {
        profile.addTeachSkill(1L, Proficiency.BEGINNER);

        profile.removeTeachSkill(1L);

        assertThat(profile.getTeachSkills()).isEmpty();
    }

    @Test
    void removesLearnSkill() {
        profile.addLearnSkill(2L);

        profile.removeLearnSkill(2L);

        assertThat(profile.getLearnSkillIds()).isEmpty();
    }

    @Test
    void removingSkillNotInListIsNotFound() {
        assertThatThrownBy(() -> profile.removeTeachSkill(99L))
                .isInstanceOf(SkillNotInListException.class);
        assertThatThrownBy(() -> profile.removeLearnSkill(99L))
                .isInstanceOf(SkillNotInListException.class);
    }

    @Test
    void returnedListsCannotBeModified() {
        profile.addTeachSkill(1L, Proficiency.BEGINNER);

        assertThatThrownBy(() -> profile.getTeachSkills().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> profile.getLearnSkillIds().add(5L))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
