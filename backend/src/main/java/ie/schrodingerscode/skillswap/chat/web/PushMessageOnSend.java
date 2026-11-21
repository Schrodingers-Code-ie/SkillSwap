package ie.schrodingerscode.skillswap.chat.web;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageSent;
import ie.schrodingerscode.skillswap.chat.web.dto.MessageResponse;

/**
 * Pushes every saved message to both people on /user/queue/messages, in the same JSON as the REST API.
 * The receiver sees it in their chat, and the sender's other open tabs see it too.
 */
@Component
public class PushMessageOnSend {

    private final SimpMessagingTemplate messaging;

    public PushMessageOnSend(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    //AFTER_COMMIT: only once the message is in the database, never for one that failed to save
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageSent event) {
        Message message = event.message();
        MessageResponse response = MessageResponse.from(message);
        messaging.convertAndSendToUser(String.valueOf(message.receiverId()), "/queue/messages", response);
        messaging.convertAndSendToUser(String.valueOf(message.senderId()), "/queue/messages", response);
    }
}
