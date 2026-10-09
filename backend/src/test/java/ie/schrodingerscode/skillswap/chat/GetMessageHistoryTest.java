package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;
import ie.schrodingerscode.skillswap.chat.application.GetMessageHistory;

/**
 * Tests retrieving message history between users.
 */
class GetMessageHistoryTest {

    private final InMemoryMessageRepository repo = new InMemoryMessageRepository();

    /**
     * Creates the service
     * 
     * @return a service for retrieving message history as that user
     */
    private GetMessageHistory historyAs(boolean connected) {
        return new GetMessageHistory(repo, (a, b) -> connected);
    }

    /**
     * A connected user gets the messages between the two users, in both directions,
     * and no one else's.
     */
    @Test
    void returnsConversationWhenConnected() {
        repo.save(Message.create(1, 2, new MessageText("Hi"), Instant.now()));
        repo.save(Message.create(2, 1, new MessageText("Hello"), Instant.now()));
        repo.save(Message.create(3, 4, new MessageText("Other chat"), Instant.now()));
        List<Message> history = historyAs(true).execute(1, 2);
        assertThat(history).hasSize(2);
    }

    /** Messages are returned oldest first, even if they were saved out of order. */
    @Test
    void returnsOldestFirst() {
        Instant t0 = Instant.parse("2026-01-01T10:00:00Z");
        // saved out of order on purpose
        repo.save(Message.create(1, 2, new MessageText("second"), t0.plusSeconds(10)));
        repo.save(Message.create(2, 1, new MessageText("first"), t0));
        assertThat(historyAs(true).execute(1, 2)).extracting(m -> m.text().value()).containsExactly("first", "second");
    }

    /**
     * Reading history with a user you are not connected to throws
     * NotConnectedException (403).
     */
    @Test
    void notConnectedThrows() {
        assertThatThrownBy(() -> historyAs(false).execute(1, 2)).isInstanceOf(NotConnectedException.class);
    }
}