package ie.schrodingerscode.skillswap.chat.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.application.ListChats;
import ie.schrodingerscode.skillswap.chat.web.dto.ChatSummaryResponse;

/**
 * REST controller for the chat list: every connection with the last message in that conversation.
 * Sorting and filtering happen in ListChats; this class only translates.
 */
@RestController
public class ChatListController {

    private final CurrentUser currentUser;
    private final ListChats listChats;

    /**
     * @param currentUser gives the id of the logged-in user
     * @param listChats   service that builds the chat list
     */
    public ChatListController(CurrentUser currentUser, ListChats listChats) {
        this.currentUser = currentUser;
        this.listChats = listChats;
    }

    /**
     * Returns the logged-in user's chats: chats with messages first (newest first),
     * then connections without messages (by display name).
     *
     * @return one entry per connection
     */
    @GetMapping("/api/chats")
    public List<ChatSummaryResponse> list() {
        return listChats.execute(currentUser.getId()).stream().map(ChatSummaryResponse::from).toList();
    }
}
