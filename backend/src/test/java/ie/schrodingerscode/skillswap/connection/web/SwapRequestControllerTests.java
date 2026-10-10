package ie.schrodingerscode.skillswap.connection.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import ie.schrodingerscode.skillswap.auth.HeaderCurrentUser;
import ie.schrodingerscode.skillswap.connection.application.SwapRequestService;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.exception.DuplicateRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.InvalidRequestStateException;
import ie.schrodingerscode.skillswap.connection.domain.exception.NotRequestReceiverException;
import ie.schrodingerscode.skillswap.connection.domain.exception.RequestNotFoundException;
import ie.schrodingerscode.skillswap.connection.domain.exception.SelfRequestException;

/**
 * Web layer tests with the application service mocked, as in STRUCTURE.md.
 * These only check that the controller turns requests into the right service calls,
 * and results or exceptions into the right status codes and JSON.
 * The rules themselves are tested in SwapRequestTests and SwapRequestServiceTests.
 */
@WebMvcTest(SwapRequestController.class)
@Import(HeaderCurrentUser.class) //@WebMvcTest skips @Component classes, so add the current user stub by hand
class SwapRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SwapRequestService service;

    private ResultActions sendRequest(long from, long to) throws Exception {
        return mockMvc.perform(post("/api/requests")
                .header("X-User-Id", String.valueOf(from))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\": " + to + "}"));
    }

    private ResultActions accept(long requestId, long asUser) throws Exception {
        return mockMvc.perform(post("/api/requests/" + requestId + "/accept")
                .header("X-User-Id", String.valueOf(asUser)));
    }

    @Test
    void sendingReturns201AndTheRequest() throws Exception {
        when(service.send(1L, 2L)).thenReturn(SwapRequest.create(5, 1, 2));

        sendRequest(1, 2)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.receiverId").value(2))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void requestToYourselfReturns400() throws Exception {
        when(service.send(3L, 3L)).thenThrow(new SelfRequestException());

        sendRequest(3, 3).andExpect(status().isBadRequest());
    }

    @Test
    void duplicateRequestReturns409() throws Exception {
        when(service.send(2L, 1L)).thenThrow(new DuplicateRequestException());

        sendRequest(2, 1).andExpect(status().isConflict());
    }

    @Test
    void missingReceiverIdReturns400() throws Exception {
        mockMvc.perform(post("/api/requests")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingUserHeaderReturns401() throws Exception {
        mockMvc.perform(post("/api/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\": 2}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptingReturns200AndAcceptedRequest() throws Exception {
        SwapRequest accepted = SwapRequest.create(5, 1, 2);
        accepted.accept(2);
        when(service.accept(5L, 2L)).thenReturn(accepted);

        accept(5, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void acceptingSomeoneElsesRequestReturns403() throws Exception {
        when(service.accept(5L, 3L)).thenThrow(new NotRequestReceiverException());

        accept(5, 3).andExpect(status().isForbidden());
    }

    @Test
    void acceptingTwiceReturns409() throws Exception {
        when(service.accept(5L, 2L)).thenThrow(new InvalidRequestStateException("Request has already been accepted"));

        accept(5, 2).andExpect(status().isConflict());
    }

    @Test
    void unknownRequestReturns404() throws Exception {
        when(service.accept(999L, 2L)).thenThrow(new RequestNotFoundException());

        accept(999, 2).andExpect(status().isNotFound());
    }

    @Test
    void decliningReturns204() throws Exception {
        mockMvc.perform(post("/api/requests/5/decline").header("X-User-Id", "2"))
                .andExpect(status().isNoContent());

        verify(service).decline(5L, 2L); //the controller passed the right request and user to the service
    }

    @Test
    void pendingRequestsAreSplitIntoIncomingAndOutgoing() throws Exception {
        //user 50 sent one request to 51, and got one from 52
        when(service.pendingRequests(50L)).thenReturn(List.of(
                SwapRequest.create(1, 50, 51),
                SwapRequest.create(2, 52, 50)));

        mockMvc.perform(get("/api/requests").header("X-User-Id", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outgoing[0].receiverId").value(51))
                .andExpect(jsonPath("$.incoming[0].senderId").value(52));
    }

    @Test
    void connectionsShowTheOtherUser() throws Exception {
        SwapRequest accepted = SwapRequest.create(5, 1, 2);
        accepted.accept(2);
        when(service.connections(1L)).thenReturn(List.of(accepted));
        when(service.connections(2L)).thenReturn(List.of(accepted));

        mockMvc.perform(get("/api/connections").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
        mockMvc.perform(get("/api/connections").header("X-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }
}
