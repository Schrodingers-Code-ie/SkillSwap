package ie.schrodingerscode.skillswap.connection.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.connection.domain.RequestStatus;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;

/**
 * Runs against a real Postgres in Docker (Testcontainers), as STRUCTURE.md says for infrastructure.
 * Checks the repository and the rules the database itself enforces.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class JpaSwapRequestRepositoryTests {

    @Autowired
    private SwapRequestRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    private long alice;
    private long bob;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM swap_requests");
        //swap_requests has foreign keys to users, so the users have to exist first
        alice = insertUser();
        bob = insertUser();
    }

    //TODO: update the columns here once SCRUM-57 changes the users table
    private long insertUser() {
        return jdbc.queryForObject(
                "INSERT INTO users (email, password_hash, display_name) VALUES (?, 'hash', 'Test User') RETURNING id",
                Long.class,
                UUID.randomUUID() + "@test.com"); //random email, because email is unique
    }

    @Test
    void savingANewRequestGivesItAnIdFromTheDatabase() {
        SwapRequest saved = repository.save(SwapRequest.create(alice, bob));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void acceptedStatusIsStored() {
        SwapRequest saved = repository.save(SwapRequest.create(alice, bob));
        saved.accept(bob);
        repository.save(saved);

        SwapRequest loaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    void findByPairWorksInBothDirections() {
        repository.save(SwapRequest.create(alice, bob));

        assertThat(repository.findByPair(UserPair.of(alice, bob))).isPresent();
        assertThat(repository.findByPair(UserPair.of(bob, alice))).isPresent();
    }

    @Test
    void findAllInvolvingReturnsSentAndReceived() {
        long carol = insertUser();
        repository.save(SwapRequest.create(alice, bob));
        repository.save(SwapRequest.create(carol, alice));

        assertThat(repository.findAllInvolving(alice)).hasSize(2);
        assertThat(repository.findAllInvolving(bob)).hasSize(1);
    }

    @Test
    void deleteRemovesTheRow() {
        SwapRequest saved = repository.save(SwapRequest.create(alice, bob));
        repository.delete(saved);

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void reversedPairIsRejectedByTheDatabase() {
        //the service would normally stop this first, here we go straight to the repository
        repository.save(SwapRequest.create(alice, bob));

        assertThatThrownBy(() -> repository.save(SwapRequest.create(bob, alice)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void requestToYourselfIsRejectedByTheDatabase() {
        //SwapRequest.create already blocks this, so insert with SQL to test the CHECK constraint itself
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO swap_requests (sender_id, receiver_id, status) VALUES (?, ?, 'PENDING')",
                alice, alice))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
