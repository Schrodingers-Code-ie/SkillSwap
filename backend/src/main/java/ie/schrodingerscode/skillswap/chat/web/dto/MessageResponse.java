package ie.schrodingerscode.skillswap.chat.web.dto;

import java.time.Instant;

import ie.schrodingerscode.skillswap.chat.domain.Message;

/**
 * JSON shape of one message, used by the REST API and by the WebSocket push
 * ({@code /user/queue/messages}), so the frontend handles both the same way.
 * Keeps the API format separate from the domain {@link Message}.
 *
 * @param id         the unique id assigned to the message
 * @param senderId   the id of the user who sent the message
 * @param receiverId the id of the user who received the message
 * @param text       the content of the message
 * @param sentAt     the date and time the message was sent
 */
public record MessageResponse(long id, long senderId, long receiverId, String text, Instant sentAt) {

    /**
     * Converts a domain message into the JSON shape.
     *
     * @param message the saved domain message (with an id)
     * @return the response record
     */
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.id(), message.senderId(), message.receiverId(), message.text().value(),
                message.sentAt());
    }
}
