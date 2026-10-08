package ie.schrodingerscode.skillswap.chat.application;

import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;

/**
 * Application service for loading a user's message history with another user,
 * oldest first.
 * Handles the message-history use case by identifying the current user and
 * retrieving the conversation from storage.
 */
@Service
public class GetMessageHistory {
    private final MessageRepository messages;
    private final CurrentUser currentUser;

    /**
     * @param messages    repository used to retrieve messages
     * @param currentUser provides the id of the currently logged-in user
     */
    public GetMessageHistory(MessageRepository messages, CurrentUser currentUser) {
        this.messages = messages;
        this.currentUser = currentUser;
    }

    /**
     * @param otherUserId ID of the other user in the conversation
     * @return messages exchanged between the two users
     */
    public List<Message> execute(long otherUserId) {
        return messages.findConversation(currentUser.getId(), otherUserId);
    }

}
