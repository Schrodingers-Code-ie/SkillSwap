package ie.schrodingerscode.skillswap.chat.web.dto;

import java.time.Instant;

import ie.schrodingerscode.skillswap.chat.domain.Message;

/**
 * The last message of a chat, as shown in the chat list (GET /api/chats).
 * Has no receiverId: in the chat list the receiver is always either me or the connection,
 * and senderId already tells the frontend which one sent it.
 *
 * @param id       the message id
 * @param senderId who sent it (the logged-in user or the connection)
 * @param text     the message text
 * @param sentAt   when it was sent
 */
public record LastMessageResponse(long id, long senderId, String text, Instant sentAt) {

    /**
     * @param message the domain message
     * @return the DTO
     */
    public static LastMessageResponse from(Message message) {
        return new LastMessageResponse(message.id(), message.senderId(), message.text().value(), message.sentAt());
    }
}
