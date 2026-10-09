package ie.schrodingerscode.skillswap.chat.application;

import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.chat.domain.ConnectionChecker;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;

/**
 * Application service for loading the message history between two users,
 * oldest first.
 * Checks the two users are connected and retrieves the conversation from
 * storage.
 * The id of the user asking is passed in by the caller (the controller).
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
     * @param userId      id of the user reading the history
     * @param otherUserId ID of the other user in the conversation
     * @return messages exchanged between the two users
     * @throws NotConnectedException if the users are not connected
     */
    public List<Message> execute(long userId, long otherUserId) {
        if (!connections.areConnected(userId, otherUserId)) {
            throw new NotConnectedException(); // 403
        }
        return messages.findConversation(userId, otherUserId);
    }

}
