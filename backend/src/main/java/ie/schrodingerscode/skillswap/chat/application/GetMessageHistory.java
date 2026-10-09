package ie.schrodingerscode.skillswap.chat.application;

import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.domain.ConnectionChecker;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;

/**
 * Application service for loading a user's message history with another user,
 * oldest first.
 * Handles the message-history use case by identifying the current user and
 * retrieving the conversation from storage.
 * For now on user can view message history with everyone, including past
 * connections.
 */
@Service
public class GetMessageHistory {
    private final MessageRepository messages;
    private final ConnectionChecker connections;
    private final CurrentUser currentUser;

    /**
     * @param messages    repository used to retrieve messages
     * @param connections checks whether users are connected
     * @param currentUser provides the id of the currently logged-in user
     */
    public GetMessageHistory(MessageRepository messages, ConnectionChecker connections, CurrentUser currentUser) {
        this.messages = messages;
        this.connections = connections;
        this.currentUser = currentUser;
    }

    /**
     * @param otherUserId ID of the other user in the conversation
     * @return messages exchanged between the two users
     * @throws NotConnectedException if the users are not connected
     */
    public List<Message> execute(long otherUserId) {
        long me = currentUser.getId(); // 401 if nobody is logged in
        if (!connections.areConnected(me, otherUserId)) {
            throw new NotConnectedException(); // 403
        }
        return messages.findConversation(me, otherUserId);
    }

}
