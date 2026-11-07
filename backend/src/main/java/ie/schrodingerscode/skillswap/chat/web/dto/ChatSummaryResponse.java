package ie.schrodingerscode.skillswap.chat.web.dto;

import ie.schrodingerscode.skillswap.chat.application.ChatSummary;

/**
 * One entry of the chat list returned by GET /api/chats.
 *
 * @param userId      id of the connection
 * @param displayName their display name (null if they have no profile yet)
 * @param lastMessage the newest message in the conversation, or null if there are no messages
 */
public record ChatSummaryResponse(long userId, String displayName, LastMessageResponse lastMessage) {

    /**
     * @param chat the chat summary
     * @return the DTO
     */
    public static ChatSummaryResponse from(ChatSummary chat) {
        LastMessageResponse last = chat.hasMessages() ? LastMessageResponse.from(chat.lastMessage()) : null;
        return new ChatSummaryResponse(chat.userId(), chat.displayName(), last);
    }
}
