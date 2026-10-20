package ie.schrodingerscode.skillswap.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

//plain JUnit with the fake repository, no Spring needed
class UserCheckerServiceTests {

    private final UserRepository repository = new FakeUserRepository();
    private final UserChecker checker = new UserCheckerService(repository);

    @Test
    void savedUserExists() {
        User saved = repository.save(User.register(new Email("ann@mail.com"), "ann", "hash", true));

        assertThat(checker.exists(saved.getId())).isTrue();
    }

    @Test
    void unknownUserDoesNotExist() {
        assertThat(checker.exists(999)).isFalse();
    }
}
