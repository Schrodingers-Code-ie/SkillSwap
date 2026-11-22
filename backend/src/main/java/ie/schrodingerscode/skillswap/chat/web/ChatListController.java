package ie.schrodingerscode.skillswap.chat.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.application.ListChats;
import ie.schrodingerscode.skillswap.chat.web.dto.ChatSummaryResponse;

/**
 * REST endpoint for the chat list: every connection with the last message in that conversation.
 * Sorting and filtering happen in {@link ListChats}; this class only translates to JSON.
 */
@RestController
public class ChatListController {

    private final CurrentUser currentUser;
    private final ListChats listChats;

    /**
     * @param currentUser gives the id of the logged-in user
     * @param listChats   use case that builds the chat list
     */
    public ChatListController(CurrentUser currentUser, ListChats listChats) {
        this.currentUser = currentUser;
        this.listChats = listChats;
    }

    /**
     * {@code GET /api/chats}: the logged-in user's chats. Chats with messages come first (newest
     * first), then connections without messages (by display name).
     *
     * @return one entry per connection; an empty list if the user has no connections
     */
    @GetMapping("/api/chats")
    public List<ChatSummaryResponse> list() {
        return listChats.execute(currentUser.getId()).stream().map(ChatSummaryResponse::from).toList();
    }
}
