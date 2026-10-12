package ie.schrodingerscode.skillswap.chat.web.dto;

import java.time.Instant;

import ie.schrodingerscode.skillswap.chat.domain.Message;

/**
 * DTO representing a message returned by the chat API.
 * Separates the API response format from the internal domain model.
 *
 * @param id         the unique ID assigned to the message
 * @param senderId   the ID of the user who sent the message
 * @param receiverId the ID of the user who received the message
 * @param text       the content of the message
 * @param sentAt     the date and time the message was sent
 */
public record MessageResponse(long id, long senderId, long receiverId, String text, Instant sentAt) {

    /**
     * Converts a domain Message into a MessageResponse DTO.
     *
     * @param message the domain message to convert
     * @return a DTO containing the message data required by the API
     */
    public static MessageResponse from(Message message) {
        return new MessageResponse(message.id(), message.senderId(), message.receiverId(), message.text().value(),
                message.sentAt());
    }
}
