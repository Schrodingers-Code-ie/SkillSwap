package ie.schrodingerscode.skillswap.chat.domain;

/**
 * Event published by {@code SendMessage} after a message is saved.
 *
 * <p>It lets other code react to a new message (for example, pushing it over WebSocket) without
 * {@code SendMessage} knowing about that code.
 *
 * @param message the saved message, with its id
 */
public record MessageSent(Message message) {
}
