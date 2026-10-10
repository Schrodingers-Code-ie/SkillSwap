package ie.schrodingerscode.skillswap.connection.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.connection.application.SwapRequestService;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.web.dto.ConnectionResponse;
import ie.schrodingerscode.skillswap.connection.web.dto.PendingRequestsResponse;
import ie.schrodingerscode.skillswap.connection.web.dto.SendRequestBody;
import ie.schrodingerscode.skillswap.connection.web.dto.SwapRequestResponse;
import jakarta.validation.Valid;

/**
 * Endpoints for swap requests and connections.
 * No rules in here: it gets the current user, calls the service and turns the result into JSON.
 */
@RestController
@RequestMapping("/api")
public class SwapRequestController {

    private final SwapRequestService service;
    private final CurrentUser currentUser;

    public SwapRequestController(SwapRequestService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public SwapRequestResponse send(@Valid @RequestBody SendRequestBody body) {
        SwapRequest request = service.send(currentUser.getId(), body.receiverId());
        return SwapRequestResponse.from(request);
    }

    @PostMapping("/requests/{id}/accept")
    public SwapRequestResponse accept(@PathVariable("id") long id) {
        SwapRequest request = service.accept(id, currentUser.getId());
        return SwapRequestResponse.from(request);
    }

    @PostMapping("/requests/{id}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT) //declined requests are deleted, so there is nothing to send back
    public void decline(@PathVariable("id") long id) {
        service.decline(id, currentUser.getId());
    }

    @GetMapping("/requests")
    public PendingRequestsResponse pending() {
        long me = currentUser.getId();
        List<SwapRequest> pending = service.pendingRequests(me);

        List<SwapRequestResponse> incoming = pending.stream()
                .filter(r -> r.getReceiverId() == me)
                .map(SwapRequestResponse::from)
                .toList();
        List<SwapRequestResponse> outgoing = pending.stream()
                .filter(r -> r.getSenderId() == me)
                .map(SwapRequestResponse::from)
                .toList();

        return new PendingRequestsResponse(incoming, outgoing);
    }

    @GetMapping("/connections")
    public List<ConnectionResponse> connections() {
        long me = currentUser.getId();
        return service.connections(me).stream()
                .map(r -> new ConnectionResponse(r.otherUser(me)))
                .toList();
    }
}
