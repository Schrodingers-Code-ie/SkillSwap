package ie.schrodingerscode.skillswap.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.application.GetMessageHistory;
import ie.schrodingerscode.skillswap.chat.application.SendMessage;
import ie.schrodingerscode.skillswap.chat.web.ChatController;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.domain.exception.InvalidMessageTextException;
import ie.schrodingerscode.skillswap.chat.domain.exception.NotConnectedException;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

/**
 * HTTP-level tests for the chat endpoints. They check only the web layer.
 * <p>
 * The use cases are mocked: @MockitoBean replaces the real SendMessage and GetMessageHistory with fakes whose behavior
 * each test scripts. For example, when(sendMessage.execute(1, 4,
 * "Hi")).thenThrow(new NotConnectedException())
 * means "when the controller calls the use case with these arguments, pretend
 * it
 * throws NotConnectedException". The test then checks only what the controller
 * is
 * responsible for:
 * - the right status code and JSON come out,
 * - the logged-in user (CurrentUser) becomes the sender id,
 * - the @NotNull shape check on the request DTO is enforced.
 * Whether a real non-connection is rejected, or whether text over 2000
 * characters
 * fails, is tested where those rules live (SendMessageTests, MessageTextTests).
 */
@WebMvcTest(ChatController.class)
class ChatControllerTests {
    @Autowired
    private MockMvc mockMvc;

    /** Fake logged-in user: each test decides who is logged in (or that nobody is). */
    @MockitoBean
    private CurrentUser currentUser;

    /** Fake use case: each test decides what it returns or throws. */
    @MockitoBean
    private SendMessage sendMessage;

    /** Fake use case: each test decides what it returns or throws. */
    @MockitoBean
    private GetMessageHistory getMessageHistory;

    private final Instant t0 = Instant.parse("2026-01-01T10:00:00Z");

    /** Builds a saved message (with an id) to use as a fake use case result. */
    private Message message(long id, long from, long to, String text, Instant at) {
        return new Message(id, from, to, new MessageText(text), at);
    }

    /**
     * POST /api/chats/{to}/messages as logged-in user {from}; a null body sends no body at
     * all.
     */
    private ResultActions send(long from, String to, String body) throws Exception {
        when(currentUser.getId()).thenReturn(from);
        var request = post("/api/chats/" + to + "/messages")
                .contentType(MediaType.APPLICATION_JSON);
        if (body != null) {
            request.content(body);
        }
        return mockMvc.perform(request);
    }

    /** GET /api/chats/{with}/messages as logged-in user {as}. */
    private ResultActions history(long as, String with) throws Exception {
        when(currentUser.getId()).thenReturn(as);
        return mockMvc.perform(get("/api/chats/" + with + "/messages"));
    }

    /** Builds the JSON body "text":"...". */
    private static String text(String value) {
        return "{\"text\":\"" + value + "\"}";
    }

    /**
     * The logged-in user (CurrentUser.getId()) is passed as the sender, and the saved message is returned
     * with 201.
     */
    @Test
    void sendReturns201() throws Exception {
        when(sendMessage.execute(1, 2, "Hi")).thenReturn(message(7, 1, 2, "Hi", t0));
        send(1, "2", text("Hi")).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.senderId").value(1)).andExpect(jsonPath("$.receiverId").value(2))
                .andExpect(jsonPath("$.text").value("Hi"));
    }

    /** NotConnectedException from the use case becomes 403. */
    @Test
    void sendToNonConnectionReturns403() throws Exception {
        when(sendMessage.execute(1, 4, "Hi")).thenThrow(new NotConnectedException());
        send(1, "4", text("Hi")).andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403));
    }

    /** InvalidMessageTextException from the domain becomes 400 with its message. */
    @Test
    void invalidTextReturns400() throws Exception {
        when(sendMessage.execute(anyLong(), anyLong(), anyString()))
                .thenThrow(new InvalidMessageTextException("Message text must not be empty"));
        send(1, "2", text("")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Message text must not be empty"));
    }

    /**
     * A missing text field fails the @NotNull shape check in the request DTO,
     * so the use case is never called. The receiver is user 4, a non-connection, on
     * purpose: it pins that the shape check runs BEFORE the connection check, so
     * this
     * gives 400, not 403. (Empty or too-long text is different: those are domain
     * rules, so for a non-connection they still give 403 first.)
     */
    @Test
    void missingTextFieldReturns400BeforeConnectionCheck() throws Exception {
        send(1, "4", "{}").andExpect(status().isBadRequest());
        verifyNoInteractions(sendMessage); // never reached the use case
    }

    /** A POST without a body returns 400. */
    @Test
    void missingBodyReturns400() throws Exception {
        send(1, "2", null).andExpect(status().isBadRequest());
    }

    /** A non-numeric user id in the URL returns 400. */
    @Test
    void nonNumericUserIdReturns400() throws Exception {
        send(1, "abc", text("Hi")).andExpect(status().isBadRequest());
    }

    /** When nobody is logged in, CurrentUser throws and the request returns 401. */
    @Test
    void notLoggedInReturns401() throws Exception {
        when(currentUser.getId()).thenThrow(new UnauthorizedException("Not logged in"));
        mockMvc.perform(post("/api/chats/2/messages").contentType(MediaType.APPLICATION_JSON)
                .content(text("Hi"))).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        verifyNoInteractions(sendMessage);
    }

    /** History also returns 401 when nobody is logged in. */
    @Test
    void historyNotLoggedInReturns401() throws Exception {
        when(currentUser.getId()).thenThrow(new UnauthorizedException("Not logged in"));
        mockMvc.perform(get("/api/chats/2/messages")).andExpect(status().isUnauthorized());
        verifyNoInteractions(getMessageHistory);
    }

    /** History is returned in the order the use case gives it (oldest first). */
    @Test
    void historyReturnsMessagesInOrder() throws Exception {
        when(getMessageHistory.execute(1, 2)).thenReturn(
                List.of(message(1, 1, 2, "first", t0), message(2, 2, 1, "second", t0.plusSeconds(10))));
        history(1, "2").andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].text").value("first"))
                .andExpect(jsonPath("$[1].text").value("second"));
    }

    /** NotConnectedException from the use case becomes 403 for history too. */
    @Test
    void historyWithNonConnectionReturns403() throws Exception {
        when(getMessageHistory.execute(1, 4)).thenThrow(new NotConnectedException());
        history(1, "4").andExpect(status().isForbidden());
    }
}
