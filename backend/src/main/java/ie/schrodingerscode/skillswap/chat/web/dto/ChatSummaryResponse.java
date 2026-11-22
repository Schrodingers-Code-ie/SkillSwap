package ie.schrodingerscode.skillswap.chat.web.dto;

import ie.schrodingerscode.skillswap.chat.application.ChatSummary;

/**
 * One entry of the chat list returned by {@code GET /api/chats}.
 *
 * @param userId      id of the connection
 * @param displayName their display name (null if they have no profile yet)
 * @param lastMessage the newest message in the conversation, or null if there are no messages
 */
public record ChatSummaryResponse(long userId, String displayName, LastMessageResponse lastMessage) {

    /**
     * Converts a chat list entry from the use case into the JSON shape.
     *
     * @param chat the chat summary
     * @return the response record
     */
    public static ChatSummaryResponse from(ChatSummary chat) {
        LastMessageResponse last = chat.hasMessages() ? LastMessageResponse.from(chat.lastMessage()) : null;
        return new ChatSummaryResponse(chat.userId(), chat.displayName(), last);
    }
}
