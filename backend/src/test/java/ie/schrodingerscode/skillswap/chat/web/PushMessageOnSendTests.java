package ie.schrodingerscode.skillswap.chat.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageSent;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.web.dto.MessageResponse;

class PushMessageOnSendTests {

    private final SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
    private final PushMessageOnSend push = new PushMessageOnSend(messaging);

    @Test
    void pushesTheMessageToReceiverAndSender() {
        Message message = new Message(7L, 1, 2, new MessageText("Hi"), Instant.parse("2026-01-01T10:00:00Z"));

        push.on(new MessageSent(message));

        MessageResponse expected = MessageResponse.from(message);
        verify(messaging).convertAndSendToUser("2", "/queue/messages", expected);
        verify(messaging).convertAndSendToUser("1", "/queue/messages", expected);
        verifyNoMoreInteractions(messaging);
    }
}
