package ie.schrodingerscode.skillswap.chat.application;

import java.time.Instant;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.connection.application.ConnectionChecker;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;

/**
 * Application service for sending a message to a connection.
 * Checks the two users are connected, validates the message text, and saves
 * the message. The sender id is passed in by the caller (the controller).
 */
@Service
public class SendMessage {
    private final MessageRepository messages;
    private final ConnectionChecker connections;

    /**
     * @param messages    repository used to save messages
     * @param connections checks whether users are connected
     */
    public SendMessage(MessageRepository messages, ConnectionChecker connections) {
        this.messages = messages;
        this.connections = connections;
    }

    /**
     * @param senderId   id of the user sending the message
     * @param receiverId id of the user receiving the message
     * @param rawText    message text provided by the user
     * @return the saved message with its assigned id
     * @throws NotConnectedException if the users are not connected
     */
    public Message execute(long senderId, long receiverId, String rawText) {
        if (!connections.areConnected(senderId, receiverId)) {
            throw new NotConnectedException(); // 403
        }
        MessageText text = new MessageText(rawText); // 400 if empty or too long
        return messages.save(Message.create(senderId, receiverId, text, Instant.now()));
    }

}
