package ie.schrodingerscode.skillswap.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidDisplayNameException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidUsernameException;

//plain unit tests for the registration rules on the User aggregate
class UserTests {

    private static final Email EMAIL = new Email("ann@mail.com");
    private static final String USERNAME = "ann";

    @Test
    void registerCreatesUnsavedUserWithTrimmedName() {
        User user = User.register(EMAIL, USERNAME, "hash", "  Ann  ", true);

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getDisplayName()).isEqualTo("Ann");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void registerWithout18PlusConfirmationFails() {
        assertThatThrownBy(() -> User.register(EMAIL, USERNAME, "hash", "Ann", false))
                .isInstanceOf(AgeNotConfirmedException.class);
    }

    @Test
    void registerWithMissingDisplayNameFails() {
        assertThatThrownBy(() -> User.register(EMAIL, USERNAME, "hash", null, true))
                .isInstanceOf(InvalidDisplayNameException.class);
        assertThatThrownBy(() -> User.register(EMAIL, USERNAME, "hash", "   ", true))
                .isInstanceOf(InvalidDisplayNameException.class);
    }

    @Test
    void displayNameCanBe100CharactersButNot101() {
        assertThat(User.register(EMAIL, USERNAME, "hash", "a".repeat(100), true).getDisplayName()).hasSize(100);
        assertThatThrownBy(() -> User.register(EMAIL, USERNAME, "hash", "a".repeat(101), true))
                .isInstanceOf(InvalidDisplayNameException.class);
    }

    @Test
    void usernameIsTrimmedAndKeepsItsCase() {
        User user = User.register(EMAIL, "  Ann_99  ", "hash", "Ann", true);

        assertThat(user.getUsername()).isEqualTo("Ann_99");
    }

    @Test
    void registerWithMissingUsernameFails() {
        assertThatThrownBy(() -> User.register(EMAIL, null, "hash", "Ann", true))
                .isInstanceOf(InvalidUsernameException.class);
        assertThatThrownBy(() -> User.register(EMAIL, "   ", "hash", "Ann", true))
                .isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void usernameMustBe3To20Characters() {
        assertThat(User.register(EMAIL, "abc", "hash", "Ann", true).getUsername()).hasSize(3);
        assertThat(User.register(EMAIL, "a".repeat(20), "hash", "Ann", true).getUsername()).hasSize(20);
        assertThatThrownBy(() -> User.register(EMAIL, "ab", "hash", "Ann", true))
                .isInstanceOf(InvalidUsernameException.class);
        assertThatThrownBy(() -> User.register(EMAIL, "a".repeat(21), "hash", "Ann", true))
                .isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void usernameAllowsOnlyLettersNumbersAndUnderscore() {
        for (String bad : new String[]{"ann-smith", "ann smith", "ann.smith", "ann!", "anné"}) {
            assertThatThrownBy(() -> User.register(EMAIL, bad, "hash", "Ann", true))
                    .as(bad)
                    .isInstanceOf(InvalidUsernameException.class);
        }
    }

    @Test
    void withIdReturnsCopyAndLeavesOriginalUnchanged() {
        User original = User.register(EMAIL, USERNAME, "hash", "Ann", true);

        User saved = original.withId(5);

        assertThat(saved.getId()).isEqualTo(5L);
        assertThat(saved.getEmail()).isEqualTo(original.getEmail());
        assertThat(saved.getUsername()).isEqualTo(original.getUsername());
        assertThat(saved.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(original.getId()).isNull();
    }
}
