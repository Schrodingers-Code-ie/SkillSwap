package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;

class InMemoryMessageRepositoryTest {

    private final InMemoryMessageRepository repo = new InMemoryMessageRepository();

    /** Fixed timestamp used to create predictable message times. */
    private final Instant t0 = Instant.parse("2026-01-01T10:00:00Z");

    /**
     * Creates an unsaved message for testing.
     * 
     * @param from sender's user ID
     * @param to   receiver's user ID
     * @param text message content
     * @param at   message timestamp
     * @return a new unsaved message
     */
    private Message msg(long from, long to, String text, Instant at) {
        return Message.create(from, to, new MessageText(text), at);
    }

    /** Verifies that saved messages receive increasing ids. */
    @Test
    void saveAssignsIncreasingIds() {
        Message first = repo.save(msg(1, 2, "a", t0));
        Message second = repo.save(msg(1, 2, "b", t0.plusSeconds(1)));

        assertThat(first.id()).isNotNull();
        assertThat(second.id()).isGreaterThan(first.id());
    }

    /**
     * Verifies that conversation messages are sorted by timestamp,
     * regardless of the order in which they were saved.
     */
    @Test
    void conversationIsReturnedOldestFirst() {
        // Save messages out of chronological order.
        repo.save(msg(1, 2, "third", t0.plusSeconds(30)));
        repo.save(msg(2, 1, "first", t0));
        repo.save(msg(1, 2, "second", t0.plusSeconds(10)));

        List<Message> history = repo.findConversation(1, 2);

        assertThat(history).extracting(m -> m.text().value()).containsExactly("first", "second", "third");
    }

    /** Verifies that message ids determine the order when timestamps match. */
    @Test
    void sameTimestampKeepsSaveOrder() {
        repo.save(msg(1, 2, "one", t0));
        repo.save(msg(2, 1, "two", t0));

        assertThat(repo.findConversation(1, 2)).extracting(m -> m.text().value()).containsExactly("one", "two");
    }

    /**
     * Verifies that conversations include messages in both directions,
     * regardless of which participant is passed first.
     */
    @Test
    void conversationIncludesBothDirectionsWhicheverWayYouAsk() {
        repo.save(msg(1, 2, "to 2", t0));
        repo.save(msg(2, 1, "to 1", t0.plusSeconds(1)));

        assertThat(repo.findConversation(1, 2)).hasSize(2);
        assertThat(repo.findConversation(2, 1)).hasSize(2);
    }

    /** Verifies that messages exchanged with other users are excluded. */
    @Test
    void conversationExcludesOtherUsers() {
        repo.save(msg(1, 2, "mine", t0));
        repo.save(msg(1, 3, "someone else", t0));
        repo.save(msg(3, 4, "unrelated", t0));

        assertThat(repo.findConversation(1, 2)).extracting(m -> m.text().value()).containsExactly("mine");
    }

    /** Verifies that a conversation with no messages returns an empty list. */
    @Test
    void emptyConversationReturnsEmptyList() {
        assertThat(repo.findConversation(1, 2)).isEmpty();
    }
}