package ie.schrodingerscode.skillswap.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ie.schrodingerscode.skillswap.auth.HeaderCurrentUser;
import ie.schrodingerscode.skillswap.chat.application.GetMessageHistory;
import ie.schrodingerscode.skillswap.chat.application.SendMessage;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;
import ie.schrodingerscode.skillswap.chat.infrastructure.StubConnectionChecker;
import ie.schrodingerscode.skillswap.chat.web.ChatController;

/**
 * Integration tests for the chat REST controller.
 * Tests HTTP requests and responses for sending messages and retrieving
 * conversation history, including validation and error handling.
 */
@WebMvcTest(ChatController.class)
@Import({ SendMessage.class, GetMessageHistory.class, InMemoryMessageRepository.class, StubConnectionChecker.class,
        HeaderCurrentUser.class })
class ChatControllerTests {
    /**
     * Sends mock HTTP requests to the controller without starting a real server.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * JSON request body used by tests that send a message containing "Hi".
     */
    private static final String BODY_HI = "{\"text\":\"Hi\"}";

    /**
     * Verifies that sending a message to a connected user returns HTTP 201
     * and includes the expected message details in the response.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void sendToConnectionReturns201() throws Exception {
        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content(BODY_HI))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.receiverId").value(2)).andExpect(jsonPath("$.text").value("Hi"))
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Verifies that sending a message to a user who is not connected
     * returns HTTP 403 Forbidden.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void sendToNonConnectionReturns403() throws Exception {
        mockMvc.perform(post("/api/chats/4/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content(BODY_HI)).andExpect(status().isForbidden()).andExpect(jsonPath("$.status").value(403));
    }

    /**
     * Verifies that an empty message is rejected with HTTP 400 Bad Request.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void emptyTextReturns400() throws Exception {
        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"\"}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /**
     * Verifies that a message containing only whitespace is rejected.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void whitespaceOnlyTextReturns400() throws Exception {
        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"   \"}")).andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a request without the required text field returns HTTP 400.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void missingTextFieldReturns400() throws Exception {
        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a message exceeding the maximum permitted length is rejected.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void textOverLimitReturns400() throws Exception {
        String tooLong = "a".repeat(2001);
        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"" + tooLong + "\"}")).andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a POST request without a body returns HTTP 400 Bad Request.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void missingBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a non-numeric user ID in the URL returns HTTP 400 Bad Request.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void nonNumericUserIdReturns400() throws Exception {
        mockMvc.perform(post("/api/chats/abc/messages").header("X-User-Id", "1").contentType(MediaType.APPLICATION_JSON)
                .content(BODY_HI)).andExpect(status().isBadRequest());
    }

    /**
     * Verifies that a request without the current user's ID header
     * returns HTTP 401 Unauthorized.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void missingUserHeaderReturns401() throws Exception {
        mockMvc.perform(post("/api/chats/2/messages").contentType(MediaType.APPLICATION_JSON).content(BODY_HI))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Verifies that conversation history is returned oldest first
     * and that both participants can retrieve the same conversation.
     *
     * @throws Exception if any mock HTTP request fails
     */
    @Test
    void historyIsOldestFirstAndSharedByBothUsers() throws Exception {
        // Users 2 and 3 are only used in this test, so no other test's messages leak
        // in.
        mockMvc.perform(post("/api/chats/3/messages").header("X-User-Id", "2").contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"first\"}")).andExpect(status().isCreated());

        mockMvc.perform(post("/api/chats/2/messages").header("X-User-Id", "3").contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"second\"}")).andExpect(status().isCreated());

        // Both participants should see the same messages in chronological order.
        mockMvc.perform(get("/api/chats/3/messages").header("X-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].text").value("first"))
                .andExpect(jsonPath("$[1].text").value("second"));

        mockMvc.perform(get("/api/chats/2/messages").header("X-User-Id", "3")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("first")).andExpect(jsonPath("$[1].text").value("second"));
    }

    /**
     * Verifies that requesting conversation history with a user who is not
     * connected returns HTTP 403 Forbidden.
     *
     * @throws Exception if the mock HTTP request fails
     */
    @Test
    void historyWithNonConnectionReturns403() throws Exception {
        mockMvc.perform(get("/api/chats/4/messages").header("X-User-Id", "1")).andExpect(status().isForbidden());
    }
}