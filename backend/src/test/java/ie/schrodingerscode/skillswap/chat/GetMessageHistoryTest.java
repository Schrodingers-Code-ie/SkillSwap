package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;
import ie.schrodingerscode.skillswap.chat.application.GetMessageHistory;
import ie.schrodingerscode.skillswap.auth.CurrentUser;

/**
 * Tests retrieving message history between users.
 */
class GetMessageHistoryTest {

    private final InMemoryMessageRepository repo = new InMemoryMessageRepository();

    /**
     * Creates the service with a simulated logged-in user.
     * 
     * @param userId the id of the current user
     * @return a service for retrieving message history as that user
     */
    private GetMessageHistory historyAs(long userId) {
        CurrentUser currentUser = () -> userId;
        return new GetMessageHistory(repo, currentUser);
    }

    /**
     * Verifies that the conversation is returned when messages exist
     * between the current user and the selected user.
     */
    @Test
    void returnsConversation() {
        repo.save(Message.create(1, 2, new MessageText("Hi"), Instant.parse("2026-01-01T10:00:00Z")));
        repo.save(Message.create(2, 1, new MessageText("Hello"), Instant.parse("2026-01-01T10:01:00Z")));
        repo.save(Message.create(3, 4, new MessageText("Other chat"), Instant.parse("2026-01-01T10:02:00Z")));
        List<Message> history = historyAs(1).execute(2);
        assertThat(history).hasSize(2);
        assertThat(history).extracting(m -> m.text().value()).containsExactly("Hi", "Hello");
    }

    /**
     * Verifies that a user can retrieve their message history even if
     * they are no longer connected to the other user.
     */
    @Test
    void returnsHistoryWithoutCheckingConnection() {
        repo.save(
                Message.create(1, 2, new MessageText("Previous conversation"), Instant.parse("2026-01-01T10:00:00Z")));
        List<Message> history = historyAs(1).execute(2);
        assertThat(history).extracting(m -> m.text().value()).containsExactly("Previous conversation");
    }

    /**
     * Verifies that no messages are returned when the users have no conversation
     * history.
     */
    @Test
    void returnsEmptyListWhenNoConversationExists() {
        assertThat(historyAs(1).execute(2)).isEmpty();
    }
}