package ie.schrodingerscode.skillswap.connection.infrastructure;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import ie.schrodingerscode.skillswap.connection.domain.ConnectionCreated;
import ie.schrodingerscode.skillswap.profile.application.DisplayNames;

/**
 * Pushes a new connection to both people on /user/queue/connections.
 * Each one gets the other person as { userId, displayName }.
 */
@Component
public class PushConnectionOnCreate {

    private final SimpMessagingTemplate messaging;
    private final DisplayNames displayNames;

    public PushConnectionOnCreate(SimpMessagingTemplate messaging, DisplayNames displayNames) {
        this.messaging = messaging;
        this.displayNames = displayNames;
    }

    //AFTER_COMMIT: only once the accept is in the database, never for one that failed
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ConnectionCreated event) {
        long sender = event.senderId();
        long receiver = event.receiverId();
        Map<Long, String> names = displayNames.displayNamesOf(List.of(sender, receiver));
        messaging.convertAndSendToUser(String.valueOf(sender), "/queue/connections",
                new NewConnectionPush(receiver, names.get(receiver)));
        messaging.convertAndSendToUser(String.valueOf(receiver), "/queue/connections",
                new NewConnectionPush(sender, names.get(sender)));
    }
}
