package ie.schrodingerscode.skillswap.chat.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageSent;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;

/** Tests message sending, connection checks, and text validation. */
class SendMessageTests {

    private final InMemoryMessageRepository repo = new InMemoryMessageRepository();
    /** ApplicationEventPublisher has one method, so events::add works as a fake publisher. */
    private final List<Object> events = new ArrayList<>();

    /**
     * Creates a service with a fake connection checker and current user.
     *
     * @param connected whether the users are considered connected
     * @return a configured message sending service
     */
    private SendMessage sendAs(boolean connected) {
        return new SendMessage(repo, (a, b) -> connected, events::add);
    }

    /** Verifies that a valid message is saved when the users are connected. */
    @Test
    void savesMessageWhenConnected() {
        Message sent = sendAs(true).execute(1, 2, "Hi");

        assertThat(sent.id()).isNotNull();
        assertThat(sent.senderId()).isEqualTo(1);
        assertThat(sent.receiverId()).isEqualTo(2);
        assertThat(sent.text().value()).isEqualTo("Hi");
        assertThat(repo.findConversation(1, 2)).containsExactly(sent);
    }

    /**
     * Verifies that sending to a non-connection throws an exception and saves
     * nothing.
     */
    @Test
    void notConnectedThrowsAndSavesNothing() {
        assertThatThrownBy(() -> sendAs(false).execute(1, 2, "Hi")).isInstanceOf(NotConnectedException.class);

        assertThat(repo.findConversation(1, 2)).isEmpty();
    }

    /** Verifies that invalid message text is rejected without saving a message. */
    @Test
    void invalidTextToConnectionThrowsAndSavesNothing() {
        assertThatThrownBy(() -> sendAs(true).execute(1, 2, "   ")).isInstanceOf(InvalidMessageTextException.class);

        assertThat(repo.findConversation(1, 2)).isEmpty();
    }

    /**
     * Verifies that the connection check happens before text validation.
     * An empty message to a non-connection therefore throws NotConnectedException.
     */
    @Test
    void emptyTextToNonConnectionGivesNotConnectedError() {
        assertThatThrownBy(() -> sendAs(false).execute(1, 2, "")).isInstanceOf(NotConnectedException.class);
    }

    /** Verifies that a saved message is published with MessageSent, carrying the saved message (with its id). */
    @Test
    void savedMessageIsPublished() {
        Message sent = sendAs(true).execute(1, 2, "Hi");

        assertThat(events).containsExactly(new MessageSent(sent));
    }

    /** Verifies that a rejected message is never published, so nothing is pushed for it. */
    @Test
    void rejectedMessageIsNotPublished() {
        assertThatThrownBy(() -> sendAs(false).execute(1, 2, "Hi")).isInstanceOf(NotConnectedException.class);
        assertThatThrownBy(() -> sendAs(true).execute(1, 2, "   ")).isInstanceOf(InvalidMessageTextException.class);

        assertThat(events).isEmpty();
    }
}
