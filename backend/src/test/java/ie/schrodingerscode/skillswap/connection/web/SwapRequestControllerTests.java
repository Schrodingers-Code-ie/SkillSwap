package ie.schrodingerscode.skillswap.connection.web;

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
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;

import ie.schrodingerscode.skillswap.auth.HeaderCurrentUser;
import ie.schrodingerscode.skillswap.connection.application.SwapRequestService;
import ie.schrodingerscode.skillswap.connection.infrastructure.InMemorySwapRequestRepository;

/**
 * Web layer tests, following the "How to check" steps in SCRUM-29.
 * @WebMvcTest doesn't load services or repositories by itself, so they are added with @Import.
 * The in-memory storage is shared between tests, so every test uses its own user IDs.
 */
@WebMvcTest(SwapRequestController.class)
@Import({ SwapRequestService.class, InMemorySwapRequestRepository.class, HeaderCurrentUser.class })
class SwapRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private ResultActions sendRequest(long from, long to) throws Exception {
        return mockMvc.perform(post("/api/requests")
                .header("X-User-Id", String.valueOf(from))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\": " + to + "}"));
    }

    //sends a request and returns its id from the JSON response
    private long sendAndGetId(long from, long to) throws Exception {
        String json = sendRequest(from, to)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.id")).longValue();
    }

    private ResultActions accept(long requestId, long asUser) throws Exception {
        return mockMvc.perform(post("/api/requests/" + requestId + "/accept")
                .header("X-User-Id", String.valueOf(asUser)));
    }

    @Test
    void howToCheckFromTheTicket() throws Exception {
        //as user 1, request user 2
        long requestId = sendAndGetId(1, 2);

        //as user 2, request user 1: already exists in the other direction
        sendRequest(2, 1).andExpect(status().isConflict());

        //as user 3, try to accept: not the receiver
        accept(requestId, 3).andExpect(status().isForbidden());

        //as user 2, accept it
        accept(requestId, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        //both users see each other in /api/connections
        mockMvc.perform(get("/api/connections").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
        mockMvc.perform(get("/api/connections").header("X-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));

        //accepting again: already accepted
        accept(requestId, 2).andExpect(status().isConflict());
    }

    @Test
    void sendingReturns201AndPendingRequest() throws Exception {
        sendRequest(10, 11)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senderId").value(10))
                .andExpect(jsonPath("$.receiverId").value(11))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void requestToYourselfReturns400() throws Exception {
        sendRequest(20, 20).andExpect(status().isBadRequest());
    }

    @Test
    void missingReceiverIdReturns400() throws Exception {
        mockMvc.perform(post("/api/requests")
                .header("X-User-Id", "30")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingUserHeaderReturns401() throws Exception {
        mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\": 41}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void pendingRequestsAreSplitIntoIncomingAndOutgoing() throws Exception {
        sendAndGetId(50, 51);

        mockMvc.perform(get("/api/requests").header("X-User-Id", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outgoing[0].receiverId").value(51))
                .andExpect(jsonPath("$.incoming").isEmpty());

        mockMvc.perform(get("/api/requests").header("X-User-Id", "51"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incoming[0].senderId").value(50))
                .andExpect(jsonPath("$.outgoing").isEmpty());
    }

    @Test
    void decliningReturns204AndDeletesTheRequest() throws Exception {
        long requestId = sendAndGetId(60, 61);

        //only the receiver can decline
        mockMvc.perform(post("/api/requests/" + requestId + "/decline").header("X-User-Id", "60"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/requests/" + requestId + "/decline").header("X-User-Id", "61"))
                .andExpect(status().isNoContent());

        //it's gone
        mockMvc.perform(get("/api/requests").header("X-User-Id", "61"))
                .andExpect(jsonPath("$.incoming").isEmpty());
        accept(requestId, 61).andExpect(status().isNotFound());
    }

    @Test
    void unknownRequestReturns404() throws Exception {
        accept(999999, 70).andExpect(status().isNotFound());
    }
}
