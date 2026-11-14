package ie.schrodingerscode.skillswap.chat.application;

import java.time.Instant;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ie.schrodingerscode.skillswap.connection.application.ConnectionChecker;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.chat.domain.MessageSent;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;

/**
 * Application service for sending a message to a connection.
 * Checks the two users are connected, validates the message text, saves
 * the message and publishes MessageSent. The sender id is passed in by the caller (the controller).
 */
@Service
public class SendMessage {
    private final MessageRepository messages;
    private final ConnectionChecker connections;
    private final ApplicationEventPublisher events;

    /**
     * @param messages    repository used to save messages
     * @param connections checks whether users are connected
     * @param events      publishes MessageSent after a message is saved
     */
    public SendMessage(MessageRepository messages, ConnectionChecker connections,
            ApplicationEventPublisher events) {
        this.messages = messages;
        this.connections = connections;
        this.events = events;
    }

    /**
     * @param senderId   id of the user sending the message
     * @param receiverId id of the user receiving the message
     * @param rawText    message text provided by the user
     * @return the saved message with its assigned id
     * @throws NotConnectedException if the users are not connected
     */
    // @Transactional so listeners with AFTER_COMMIT (PushMessageOnSend) run only once the message is really saved
    @Transactional
    public Message execute(long senderId, long receiverId, String rawText) {
        if (!connections.areConnected(senderId, receiverId)) {
            throw new NotConnectedException(); // 403
        }
        MessageText text = new MessageText(rawText); // 400 if empty or too long
        Message saved = messages.save(Message.create(senderId, receiverId, text, Instant.now()));
        events.publishEvent(new MessageSent(saved));
        return saved;
    }
}
