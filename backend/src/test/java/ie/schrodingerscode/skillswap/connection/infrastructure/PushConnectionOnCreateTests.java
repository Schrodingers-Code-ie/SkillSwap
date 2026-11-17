package ie.schrodingerscode.skillswap.connection.infrastructure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import ie.schrodingerscode.skillswap.connection.domain.ConnectionCreated;
import ie.schrodingerscode.skillswap.profile.application.DisplayNames;

class PushConnectionOnCreateTests {

    private final SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
    //DisplayNames has one method, so a lambda works as a fake name lookup
    private final DisplayNames displayNames = ids -> Map.of(1L, "Ann", 2L, "Ben");
    private final PushConnectionOnCreate push = new PushConnectionOnCreate(messaging, displayNames);

    @Test
    void eachPersonGetsTheOtherAsTheirNewFriend() {
        push.on(new ConnectionCreated(1, 2));

        verify(messaging).convertAndSendToUser("1", "/queue/connections", new NewConnectionPush(2, "Ben"));
        verify(messaging).convertAndSendToUser("2", "/queue/connections", new NewConnectionPush(1, "Ann"));
        verifyNoMoreInteractions(messaging);
    }
}
