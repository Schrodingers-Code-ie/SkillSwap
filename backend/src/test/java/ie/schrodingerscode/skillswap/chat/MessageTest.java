package ie.schrodingerscode.skillswap.chat;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class MessageTest {

    /* Timestamp shared by the tests. */
    private final Instant now = Instant.now();

    /* A new message has no id until it is saved. */
    @Test
    void newMessageHasNoId() {
        Message message = Message.create(1, 2, new MessageText("Hi"), now);
        assertThat(message.id()).isNull();
    }

    /* withId() returns a new message and leaves the original unchanged. */
    @Test
    void withIdReturnsCopyAndKeepsOriginalUnchanged() {
        Message original = Message.create(1, 2, new MessageText("Hi"), now);
        Message saved = original.withId(5);

        assertThat(saved.id()).isEqualTo(5);
        assertThat(saved.senderId()).isEqualTo(1);
        assertThat(saved.receiverId()).isEqualTo(2);
        assertThat(saved.text()).isEqualTo(original.text());
        assertThat(saved.sentAt()).isEqualTo(now);
        assertThat(original.id()).isNull(); // the original did not change
    }

    /* A message cannot be created without time stamp. */
    @Test
    void rejectsMissingText() {
        assertThatThrownBy(() -> Message.create(1, 2, new MessageText("Hi"), null))
                .isInstanceOf(NullPointerException.class);
    }

}
