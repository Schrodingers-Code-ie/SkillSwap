package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;
import ie.schrodingerscode.skillswap.chat.application.SendMessage;

/** Tests message sending, connection checks, and text validation. */
class SendMessageTest {

    private final InMemoryMessageRepository repo = new InMemoryMessageRepository();

    /**
     * Creates a service with a fake connection checker and current user.
     *
     * @param userId    ID of the logged-in user
     * @param connected whether the users are considered connected
     * @return a configured message sending service
     */
    private SendMessage sendAs(long userId, boolean connected) {
        return new SendMessage(repo, (a, b) -> connected, () -> userId);
    }

    /** Verifies that a valid message is saved when the users are connected. */
    @Test
    void savesMessageWhenConnected() {
        Message sent = sendAs(1, true).execute(2, "Hi");

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
        assertThatThrownBy(() -> sendAs(1, false).execute(2, "Hi")).isInstanceOf(NotConnectedException.class);

        assertThat(repo.findConversation(1, 2)).isEmpty();
    }

    /** Verifies that invalid message text is rejected without saving a message. */
    @Test
    void invalidTextToConnectionThrowsAndSavesNothing() {
        assertThatThrownBy(() -> sendAs(1, true).execute(2, "   ")).isInstanceOf(InvalidMessageTextException.class);

        assertThat(repo.findConversation(1, 2)).isEmpty();
    }

    /**
     * Verifies that the connection check happens before text validation.
     * An empty message to a non-connection therefore throws NotConnectedException.
     */
    @Test
    void emptyTextToNonConnectionGivesNotConnectedError() {
        assertThatThrownBy(() -> sendAs(1, false).execute(2, "")).isInstanceOf(NotConnectedException.class);
    }
}