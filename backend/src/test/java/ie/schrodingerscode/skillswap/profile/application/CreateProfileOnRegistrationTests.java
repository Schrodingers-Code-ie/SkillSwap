package ie.schrodingerscode.skillswap.profile.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.domain.UserRegistered;
import ie.schrodingerscode.skillswap.profile.FakeProfileRepository;
import ie.schrodingerscode.skillswap.profile.domain.Profile;

class CreateProfileOnRegistrationTests {

    @Test
    void createsEmptyProfileFromEvent() {
        FakeProfileRepository profiles = new FakeProfileRepository();

        new CreateProfileOnRegistration(profiles).on(new UserRegistered(7L, "Ann"));

        Profile profile = profiles.findById(7L).orElseThrow();
        assertThat(profile.getDisplayName()).isEqualTo("Ann");
        assertThat(profile.getBio()).isNull();
        assertThat(profile.getTeachSkills()).isEmpty();
        assertThat(profile.getLearnSkillIds()).isEmpty();
    }
}