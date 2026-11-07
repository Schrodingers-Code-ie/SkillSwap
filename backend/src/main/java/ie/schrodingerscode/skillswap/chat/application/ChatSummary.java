package ie.schrodingerscode.skillswap.chat.application;

import ie.schrodingerscode.skillswap.chat.domain.Message;

/**
 * One entry in the chat list: a connection and the last message with them.
 * This is what ListChats returns; the web layer turns it into JSON.
 *
 * @param userId      id of the connection (the other person)
 * @param displayName their display name, or null if they have no profile yet
 * @param lastMessage newest message between the two users, or null if they have never messaged
 */
public record ChatSummary(long userId, String displayName, Message lastMessage) {

    public boolean hasMessages() {
        return lastMessage != null;
    }
}
