package ie.schrodingerscode.skillswap.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.EmailAlreadyUsedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidPasswordException;
import ie.schrodingerscode.skillswap.auth.domain.exception.UsernameAlreadyUsedException;

//plain unit tests with the FakeUserRepository and BCrypt, no Spring
class RegisterUserTests {

    private static final String PASSWORD = "secret123";

    //no Spring here, so we do the dependency injection by hand
    private final UserRepository repository = new FakeUserRepository();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final RegisterUser registerUser = new RegisterUser(repository, encoder);

    @Test
    void validRegistrationSavesUserWithId() {
        User user = registerUser.register("ann@mail.com", "ann", PASSWORD, "Ann", true);

        assertThat(user.getId()).isNotNull();
        assertThat(repository.findByEmail(new Email("ann@mail.com"))).contains(user);
        assertThat(repository.findById(user.getId())).contains(user);
    }

    @Test
    void sameEmailInUpperCaseIsRejected() {
        registerUser.register("ann@mail.com", "ann", PASSWORD, "Ann", true);

        assertThatThrownBy(() -> registerUser.register("ANN@MAIL.COM", "ann2", PASSWORD, "Ann again", true))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void sameUsernameIgnoringCaseIsRejected() {
        registerUser.register("ann@mail.com", "ann", PASSWORD, "Ann", true);

        assertThatThrownBy(() -> registerUser.register("other@mail.com", "Ann", PASSWORD, "Other", true))
                .isInstanceOf(UsernameAlreadyUsedException.class);
        assertThat(repository.existsByEmail(new Email("other@mail.com"))).isFalse();
    }

    @Test
    void usernameIsStoredAsTyped() {
        User user = registerUser.register("ann@mail.com", "Ann_Smith", PASSWORD, "Ann", true);

        assertThat(user.getUsername()).isEqualTo("Ann_Smith");
        assertThat(repository.existsByUsername("ann_smith")).isTrue();
    }

    @Test
    void passwordIsStoredAsBcryptHash() {
        User user = registerUser.register("ann@mail.com", "ann", PASSWORD, "Ann", true);

        assertThat(user.getPasswordHash()).isNotEqualTo(PASSWORD);
        assertThat(user.getPasswordHash()).startsWith("$2"); //BCrypt hashes start with $2a$, $2b$ or $2y$
        assertThat(encoder.matches(PASSWORD, user.getPasswordHash())).isTrue();
    }

    @Test
    void passwordMustBe8To72Characters() {
        assertThatThrownBy(() -> registerUser.register("a@mail.com", "ann", "a".repeat(7), "Ann", true))
                .isInstanceOf(InvalidPasswordException.class);
        assertThatThrownBy(() -> registerUser.register("b@mail.com", "ann", "a".repeat(73), "Ann", true))
                .isInstanceOf(InvalidPasswordException.class);
        assertThatThrownBy(() -> registerUser.register("c@mail.com", "ann", null, "Ann", true))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void failedRegistrationSavesNothing() {
        assertThatThrownBy(() -> registerUser.register("ann@mail.com", "ann", PASSWORD, "Ann", false))
                .isInstanceOf(AgeNotConfirmedException.class);

        assertThat(repository.existsByEmail(new Email("ann@mail.com"))).isFalse();
        assertThat(repository.existsByUsername("ann")).isFalse();
    }
}
