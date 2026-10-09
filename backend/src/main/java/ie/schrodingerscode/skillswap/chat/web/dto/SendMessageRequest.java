package ie.schrodingerscode.skillswap.chat.web.dto;

/**
 * DTO representing the data required to send a message.
 * Used as the JSON request body for POST /api/chats/{userId}/messages.
 * Message content validation is handled by the domain's MessageText class.
 *
 * @param text the content of the message to send
 */
public record SendMessageRequest(String text) {
}
