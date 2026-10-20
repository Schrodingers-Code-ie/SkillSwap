package ie.schrodingerscode.skillswap.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidUsernameException;

//plain unit tests for the registration rules on the User aggregate
class UserTests {

    private static final Email EMAIL = new Email("ann@mail.com");
    private static final String USERNAME = "ann";

    @Test
    void registerCreatesUnsavedUserWithTrimmedName() {
        User user = User.register(EMAIL, USERNAME, "hash", true);

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void registerWithout18PlusConfirmationFails() {
        assertThatThrownBy(() -> User.register(EMAIL, USERNAME, "hash", false))
                .isInstanceOf(AgeNotConfirmedException.class);
    }

    @Test
    void usernameIsTrimmedAndKeepsItsCase() {
        User user = User.register(EMAIL, "  Ann_99  ", "hash", true);

        assertThat(user.getUsername()).isEqualTo("Ann_99");
    }

    @Test
    void registerWithMissingUsernameFails() {
        assertThatThrownBy(() -> User.register(EMAIL, null, "hash", true))
                .isInstanceOf(InvalidUsernameException.class);
        assertThatThrownBy(() -> User.register(EMAIL, "   ", "hash", true))
                .isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void usernameMustBe3To20Characters() {
        assertThat(User.register(EMAIL, "abc", "hash", true).getUsername()).hasSize(3);
        assertThat(User.register(EMAIL, "a".repeat(20), "hash", true).getUsername()).hasSize(20);
        assertThatThrownBy(() -> User.register(EMAIL, "ab", "hash", true))
                .isInstanceOf(InvalidUsernameException.class);
        assertThatThrownBy(() -> User.register(EMAIL, "a".repeat(21), "hash", true))
                .isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void usernameAllowsOnlyLettersNumbersAndUnderscore() {
        for (String bad : new String[]{"ann-smith", "ann smith", "ann.smith", "ann!", "anné"}) {
            assertThatThrownBy(() -> User.register(EMAIL, bad, "hash", true))
                    .as(bad)
                    .isInstanceOf(InvalidUsernameException.class);
        }
    }

    @Test
    void restoreRebuildsUserWithTheGivenValues() {
        Instant created = Instant.parse("2026-01-01T10:00:00Z");

        User user = User.restore(5L, EMAIL, USERNAME, "hash", created);

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getCreatedAt()).isEqualTo(created);
    }
}
