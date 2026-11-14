package ie.schrodingerscode.skillswap.block.infrastructure;

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
import ie.schrodingerscode.skillswap.block.domain.Block;
import ie.schrodingerscode.skillswap.block.domain.BlockRepository;

//runs against a real Postgres in Docker (Testcontainers)
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class JpaBlockRepositoryTests {

    @Autowired
    private BlockRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    private long alice;
    private long bob;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM blocks");
        alice = insertUser();
        bob = insertUser();
    }

    //username and email are both unique, so each test user gets a random one
    private long insertUser() {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        return jdbc.queryForObject(
                "INSERT INTO users (email, username, password_hash) VALUES (?, ?, 'hash') RETURNING id",
                Long.class, unique + "@test.com", unique);
    }

    @Test
    void blockIsFoundInBothDirections() {
        repository.save(Block.create(alice, bob));

        assertThat(repository.existsEitherWay(alice, bob)).isTrue();
        assertThat(repository.existsEitherWay(bob, alice)).isTrue();
    }

    @Test
    void databaseRefusesTheSameBlockTwice() {
        repository.save(Block.create(alice, bob));

        assertThatThrownBy(() -> repository.save(Block.create(alice, bob)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
