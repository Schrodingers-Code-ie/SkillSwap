package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class JpaMessageRepositoryTests {

    @Autowired
    private MessageRepository repo;

    @Autowired
    private JdbcTemplate jdbc;

    private final Instant t0 = Instant.parse("2026-01-01T10:00:00Z");

    private long a;
    private long b;
    private long c;

    @BeforeEach
    void createUsers() {
        a = insertUser();
        b = insertUser();
        c = insertUser();
    }

    private long insertUser() {
        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        return jdbc.queryForObject(
                "INSERT INTO users (email, username, password_hash) VALUES (?, ?, 'hash') RETURNING id",
                Long.class, unique + "@test.com", unique);
    }

    private Message save(long from, long to, String text, Instant at) {
        return repo.save(Message.create(from, to, new MessageText(text), at));
    }

    @Test
    void returnsOldestFirst() {
        save(a, b, "third", t0.plusSeconds(30));
        save(b, a, "first", t0);
        save(a, b, "second", t0.plusSeconds(10));
        assertThat(repo.findConversation(a, b)).extracting(m -> m.text().value())
                .containsExactly("first", "second", "third");
    }

    @Test
    void sameTimeIsOrderedById() {
        Message first = save(a, b, "one", t0);
        Message second = save(b, a, "two", t0);
        assertThat(repo.findConversation(a, b)).containsExactly(first, second);
    }

    @Test
    void worksWhicheverUserAsks() {
        Message hi = save(a, b, "hi", t0);
        Message hello = save(b, a, "hello", t0.plusSeconds(1));
        assertThat(repo.findConversation(b, a)).containsExactly(hi, hello);
    }

    @Test
    void onlyReturnsMessagesBetweenTheTwoUsers() {
        Message mine = save(a, b, "mine", t0);
        save(a, c, "other chat", t0);
        save(c, b, "other chat", t0);
        assertThat(repo.findConversation(a, b)).containsExactly(mine);
    }

    @Test
    void noMessagesGivesEmptyList() {
        assertThat(repo.findConversation(a, b)).isEmpty();
    }

    @Test
    void loadedMessageMatchesSavedOne() {
        Message saved = save(a, b, "hi", Instant.parse("2026-01-01T10:00:00.123456789Z"));
        assertThat(repo.findConversation(a, b)).containsExactly(saved);
    }
}
