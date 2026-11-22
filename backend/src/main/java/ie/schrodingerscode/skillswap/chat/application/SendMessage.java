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
 * Use case for sending a message to a connection.
 *
 * <p>Guarantees that a message is only saved when the two users are connected and the text is valid,
 * and that {@link MessageSent} is published only for a message that was saved. The sender id is
 * passed in by the controller (the logged-in user), never taken from the request body.
 *
 * <p>The method is {@code @Transactional} so that listeners running after commit
 * ({@code PushMessageOnSend}) only fire once the message is really in the database.
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
     * Checks the connection, validates the text, saves the message and publishes {@link MessageSent}.
     *
     * @param senderId   id of the user sending the message
     * @param receiverId id of the user receiving the message
     * @param rawText    message text provided by the user
     * @return the saved message with its assigned id
     * @throws NotConnectedException if the users are not connected (returned as 403)
     * @throws ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException if the text is
     *         empty, blank or longer than {@link MessageText#MAX_LENGTH} characters (returned as 400)
     */
    @Transactional
    public Message execute(long senderId, long receiverId, String rawText) {
        if (!connections.areConnected(senderId, receiverId)) {
            throw new NotConnectedException();
        }
        MessageText text = new MessageText(rawText);
        Message saved = messages.save(Message.create(senderId, receiverId, text, Instant.now()));
        events.publishEvent(new MessageSent(saved));
        return saved;
    }
}
