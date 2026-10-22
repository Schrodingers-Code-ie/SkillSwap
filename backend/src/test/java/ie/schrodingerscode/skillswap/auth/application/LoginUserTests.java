package ie.schrodingerscode.skillswap.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

class LoginUserTests {
    private final UserRepository repository = new FakeUserRepository();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private final LoginUser loginUser = new LoginUser(repository, encoder);

    private static final String PASSWORD = "secret123";

    @BeforeEach
    void saveAnn() {
        repository.save(User.register(new Email("ann@mail.com"), "ann", encoder.encode(PASSWORD), true));
    }

    @Test
    void rightEmailAndPasswordReturnsUser() {
        User user = loginUser.login("ann@mail.com", PASSWORD);

        assertThat(user.getEmail()).isEqualTo(new Email("ann@mail.com"));
    }

    @Test
    void wrongPasswordIsRejected() {
        assertThatThrownBy(() -> loginUser.login("ann@mail.com", "secretsecret"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Wrong email or password");
    }

    @Test
    void unknownEmailIsRejected() {
        assertThatThrownBy(() -> loginUser.login("bob@mail.com", PASSWORD))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Wrong email or password");
    }

    @Test
    void badEmailFormIsRejected() {
        assertThatThrownBy(() -> loginUser.login("and", PASSWORD))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Wrong email or password");
    }

    @Test
    void rightEmailCaseInsensitiveAndPasswordReturnsUser() {
        User user = loginUser.login("ANN@MAIL.COM", PASSWORD);

        assertThat(user.getEmail()).isEqualTo(new Email("ann@mail.com"));
    }
}
