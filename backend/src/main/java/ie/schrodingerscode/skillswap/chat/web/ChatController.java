package ie.schrodingerscode.skillswap.chat.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import ie.schrodingerscode.skillswap.chat.application.GetMessageHistory;
import ie.schrodingerscode.skillswap.chat.application.SendMessage;
import ie.schrodingerscode.skillswap.chat.web.dto.MessageResponse;
import ie.schrodingerscode.skillswap.chat.web.dto.SendMessageRequest;
import ie.schrodingerscode.skillswap.auth.CurrentUser;

/**
 * REST endpoints for one conversation: {@code POST} sends a message, {@code GET} loads the history.
 * The {@code {userId}} in the path is the other user in the conversation; the logged-in user always
 * comes from {@link CurrentUser}, so nobody can send as someone else.
 *
 * <p>This class only translates between HTTP and the use cases. The rules are in the application and
 * domain layers, and their exceptions are turned into HTTP errors by {@code GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/chats/{userId}/messages")
public class ChatController {

    private final CurrentUser currentUser;
    private final SendMessage sendMessage;
    private final GetMessageHistory getMessageHistory;

    /**
     * @param currentUser       gives the id of the logged-in user
     * @param sendMessage       use case that sends messages
     * @param getMessageHistory use case that loads the conversation history
     */
    public ChatController(CurrentUser currentUser, SendMessage sendMessage, GetMessageHistory getMessageHistory) {
        this.currentUser = currentUser;
        this.sendMessage = sendMessage;
        this.getMessageHistory = getMessageHistory;
    }

    /**
     * {@code POST /api/chats/{userId}/messages}: sends a message from the logged-in user to
     * {@code userId}. Returns 201 with the saved message.
     *
     * @param userId  id of the other user in the conversation
     * @param request JSON body with the message text
     * @return the saved message, with its id and time
     * @throws ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException if the two users
     *         are not connected (403)
     * @throws ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException if the text
     *         is empty or too long (400)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@PathVariable long userId, @Valid @RequestBody SendMessageRequest request) {
        return MessageResponse.from(sendMessage.execute(currentUser.getId(), userId, request.text()));
    }

    /**
     * {@code GET /api/chats/{userId}/messages}: the conversation between the logged-in user and
     * {@code userId}.
     *
     * @param userId id of the other user in the conversation
     * @return the conversation's messages, oldest first
     * @throws ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException if the two users
     *         are not connected (403)
     */
    @GetMapping
    public List<MessageResponse> history(@PathVariable long userId) {
        return getMessageHistory.execute(currentUser.getId(), userId).stream().map(MessageResponse::from).toList();
    }
}
