package ie.schrodingerscode.skillswap.chat.web.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO representing the data required to send a message.
 * Used as the JSON request body for POST /api/chats/{userId}/messages.
 * Shape check only (the field must be present). The text rules (not empty,
 * max length) are business rules and live in MessageText only.
 *
 * @param text the content of the message to send
 */

public record SendMessageRequest(@NotNull String text) {
}
