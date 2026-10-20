package ie.schrodingerscode.skillswap.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.auth.application.RegisterUser;
import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.EmailAlreadyUsedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.UsernameAlreadyUsedException;
import jakarta.transaction.Transactional;

// Starts the whole app against a real Postgres in Docker. Flyway runs V1, Hibernate validates the entity
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional // Each test is rolled back, so tests don't affect each other
public class JpaUserRepositoryTests {

    @Autowired
    private RegisterUser registerUser;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registeredUserCanBeReadBackByEmail() {
        User registered = registerUser.register("ann@mail.com", "annnn", "hello123", "Ann Smith", true);

        User found = userRepository.findByEmail(new Email("ann@mail.com")).orElseThrow();

        assertThat(found.getId()).isNotNull().isEqualTo(registered.getId());
        assertThat(found.getUsername()).isEqualTo("annnn");
        assertThat(found.getPasswordHash()).isNotEqualTo("hello123");
        assertThat(userRepository.findById(registered.getId())).isPresent();
    }

    @Test
    void secondUserWithSameEmailFails() {
        registerUser.register("ann@mail.com", "annnn", "hello123", "Ann Smith", true);

        assertThatThrownBy(() -> registerUser.register("ann@mail.com", "jane1", "secret1", "Jane Doe", true))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void secondUserWithSameUsernameInDifferentCaseFails() {
        registerUser.register("ann@mail.com", "annnn", "hello123", "Ann Smith", true);

        assertThatThrownBy(() -> registerUser.register("ann1@mail.com", "AnnnN", "hello123", "Ann Smith", true))
                .isInstanceOf(UsernameAlreadyUsedException.class);
    }

}
