package ie.schrodingerscode.skillswap.chat.application;

import java.time.Instant;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.domain.ConnectionChecker;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;

/**
 * Application service for sending a message to a connection.
 * Handles the message-sending use case by identifying the current user,
 * validating the message text, checking if connection exist, and saving the
 * message.
 */
@Service
public class SendMessage {
    private final MessageRepository messages;
    private final ConnectionChecker connections;
    private final CurrentUser currentUser;

    /**
     * @param messages    repository used to save messages
     * @param connections checks whether users are connected
     * @param currentUser provides the id of the currently logged-in user
     */
    public SendMessage(MessageRepository messages, ConnectionChecker connections, CurrentUser currentUser) {
        this.messages = messages;
        this.connections = connections;
        this.currentUser = currentUser;
    }

    /**
     * @param receiverId id of the user receiving the message
     * @param rawText    message text provided by the user
     * @return the saved message with its assigned id
     * @throws NotConnectedException if the users are not connected
     */
    public Message execute(long receiverId, String rawText) {
        long senderId = currentUser.getId(); // 401 if nobody is logged in
        if (!connections.areConnected(senderId, receiverId)) {
            throw new NotConnectedException(); // 403
        }
        MessageText text = new MessageText(rawText); // 400 if empty or too long
        return messages.save(Message.create(senderId, receiverId, text, Instant.now()));
    }

}
