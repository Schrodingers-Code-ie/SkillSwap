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
 * REST controller for sending messages and retrieving chat history.
 * Translates HTTP requests into calls to the chat application services
 * and converts domain messages into response DTOs.
 * Business rules are handled by the application and domain layers;
 * exceptions are converted into HTTP error responses by GlobalExceptionHandler.
 * The {userId} path variable identifies the other user in the conversation.
 */
@RestController
@RequestMapping("/api/chats/{userId}/messages")
public class ChatController {

    private final CurrentUser currentUser;
    private final SendMessage sendMessage;
    private final GetMessageHistory getMessageHistory;

    /**
     * Creates the controller with the application services it needs.
     *
     * @param sendMessage       service that handles sending messages
     * @param getMessageHistory service that retrieves conversation history
     */
    public ChatController(CurrentUser currentUser, SendMessage sendMessage, GetMessageHistory getMessageHistory) {
        this.currentUser = currentUser;
        this.sendMessage = sendMessage;
        this.getMessageHistory = getMessageHistory;
    }

    /**
     * Sends a message to the specified user.
     * Reads the message text from the request DTO, delegates the operation
     * to SendMessage, and converts the resulting domain message into a response
     * DTO.
     *
     * @param userId  ID of the other user in the conversation
     * @param request request DTO containing the message text
     * @return a DTO representing the saved message
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@PathVariable long userId, @Valid @RequestBody SendMessageRequest request) {
        return MessageResponse.from(sendMessage.execute(currentUser.getId(), userId, request.text()));
    }

    /**
     * Retrieves the conversation history between the current user and the specified
     * user.
     * Converts each domain message into a response DTO while preserving the order
     * returned by the application service.
     *
     * @param userId ID of the other user in the conversation
     * @return the conversation's messages, oldest first
     */
    @GetMapping
    public List<MessageResponse> history(@PathVariable long userId) {
        return getMessageHistory.execute(currentUser.getId(), userId).stream().map(MessageResponse::from).toList();
    }
}
