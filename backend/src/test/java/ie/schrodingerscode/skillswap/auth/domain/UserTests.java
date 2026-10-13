package ie.schrodingerscode.skillswap.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidDisplayNameException;

//plain unit tests for the registration rules on the User aggregate
class UserTests {

    private static final Email EMAIL = new Email("ann@mail.com");

    @Test
    void registerCreatesUnsavedUserWithTrimmedName() {
        User user = User.register(EMAIL, "hash", "  Ann  ", true);

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getDisplayName()).isEqualTo("Ann");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void registerWithout18PlusConfirmationFails() {
        assertThatThrownBy(() -> User.register(EMAIL, "hash", "Ann", false))
                .isInstanceOf(AgeNotConfirmedException.class);
    }

    @Test
    void registerWithMissingDisplayNameFails() {
        assertThatThrownBy(() -> User.register(EMAIL, "hash", null, true))
                .isInstanceOf(InvalidDisplayNameException.class);
        assertThatThrownBy(() -> User.register(EMAIL, "hash", "   ", true))
                .isInstanceOf(InvalidDisplayNameException.class);
    }

    @Test
    void displayNameCanBe100CharactersButNot101() {
        assertThat(User.register(EMAIL, "hash", "a".repeat(100), true).getDisplayName()).hasSize(100);
        assertThatThrownBy(() -> User.register(EMAIL, "hash", "a".repeat(101), true))
                .isInstanceOf(InvalidDisplayNameException.class);
    }

    @Test
    void withIdReturnsCopyAndLeavesOriginalUnchanged() {
        User original = User.register(EMAIL, "hash", "Ann", true);

        User saved = original.withId(5);

        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getEmail()).isEqualTo(original.getEmail());
        assertThat(saved.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(original.getId()).isNull();
    }
}
