package ie.schrodingerscode.skillswap.chat.application;

import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.connection.application.ConnectionChecker;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;

/**
 * Use case for loading the conversation between the logged-in user and one of their connections.
 *
 * <p>Guarantees that only connected users can read a conversation, so a user can never see messages
 * with someone they are not connected to (including someone they used to be connected to).
 * The id of the user asking is passed in by the controller, never taken from the request body.
 */
@Service
public class GetMessageHistory {
    private final MessageRepository messages;
    private final ConnectionChecker connections;

    /**
     * @param messages    repository used to retrieve messages
     * @param connections checks whether users are connected
     */
    public GetMessageHistory(MessageRepository messages, ConnectionChecker connections) {
        this.messages = messages;
        this.connections = connections;
    }

    /**
     * Returns every message between the two users, oldest first.
     *
     * @param userId      id of the user reading the history
     * @param otherUserId id of the other user in the conversation
     * @return messages exchanged between the two users, oldest first
     * @throws NotConnectedException if the users are not connected (returned as 403)
     */
    public List<Message> execute(long userId, long otherUserId) {
        if (!connections.areConnected(userId, otherUserId)) {
            throw new NotConnectedException();
        }
        return messages.findConversation(userId, otherUserId);
    }

}
