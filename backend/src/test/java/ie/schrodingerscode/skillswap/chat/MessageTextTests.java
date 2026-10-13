package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException;

public class MessageTextTests {
    /* Accepts valid message text. */
    @Test
    void acceptsNormalText() {
        assertThat(new MessageText("Hi!").value()).isEqualTo("Hi!");
    }

    /* Rejects null message text. */
    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new MessageText(null)).isInstanceOf(InvalidMessageTextException.class);
    }

    /* Rejects empty and whitespace-only text. */
    @Test
    void rejectsEmptyAndWhitespaceOnly() {
        for (String bad : new String[] { "", "   ", "\t\n" }) {
            assertThatThrownBy(() -> new MessageText(bad)).isInstanceOf(InvalidMessageTextException.class);
        }
    }

    /* Accepts text at the maximum allowed length. */
    @Test
    void acceptsExactlyMaxLength() {
        new MessageText("a".repeat(MessageText.MAX_LENGTH));
    }

    /* Rejects text exceeding the maximum allowed length. */
    @Test
    void rejectsOverMaxLength() {
        assertThatThrownBy(() -> new MessageText("a".repeat(MessageText.MAX_LENGTH + 1)))
                .isInstanceOf(InvalidMessageTextException.class);
    }

    /* Counts spaces towards the maximum length. */
    @Test
    void spacesCountTowardsLimit() {
        String text = "a" + " ".repeat(MessageText.MAX_LENGTH); // 2001 characters
        assertThatThrownBy(() -> new MessageText(text)).isInstanceOf(InvalidMessageTextException.class);
    }

}
