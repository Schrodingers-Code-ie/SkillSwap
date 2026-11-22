package ie.schrodingerscode.skillswap.chat.web.dto;

import jakarta.validation.constraints.NotNull;

/**
 * JSON body of {@code POST /api/chats/{userId}/messages}.
 * Only checks the shape (the field must be present). The text rules (not empty, max length) are
 * business rules and live in {@code MessageText} only.
 *
 * @param text the content of the message to send
 */
public record SendMessageRequest(@NotNull String text) {
}
