package ie.schrodingerscode.skillswap.connection.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import ie.schrodingerscode.skillswap.profile.application.DisplayNames;
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
    private final DisplayNames displayNames;

    public SwapRequestController(SwapRequestService service, CurrentUser currentUser, DisplayNames displayNames) {
        this.service = service;
        this.currentUser = currentUser;
        this.displayNames = displayNames;
    }

    //loads the display names of everyone in these requests with one query
    private Map<Long, String> namesFor(List<SwapRequest> requests) {
        return displayNames.displayNamesOf(requests.stream()
                .flatMap(r -> Stream.of(r.getSenderId(), r.getReceiverId()))
                .distinct()
                .toList());
    }

    //turns a request into JSON, with both display names taken from the names map
    private static SwapRequestResponse toResponse(SwapRequest request, Map<Long, String> names) {
        return SwapRequestResponse.from(request, names.get(request.getSenderId()), names.get(request.getReceiverId()));
    }

    private SwapRequestResponse toResponse(SwapRequest request) {
        return toResponse(request, namesFor(List.of(request)));
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public SwapRequestResponse send(@Valid @RequestBody SendRequestBody body) {
        SwapRequest request = service.send(currentUser.getId(), body.receiverId());
        return toResponse(request);
    }

    @PostMapping("/requests/{id}/accept")
    public SwapRequestResponse accept(@PathVariable("id") long id) {
        SwapRequest request = service.accept(id, currentUser.getId());
        return toResponse(request);
    }

    @PostMapping("/requests/{id}/decline")
    @ResponseStatus(HttpStatus.NO_CONTENT) //declined requests are deleted, so there is nothing to send back
    public void decline(@PathVariable("id") long id) {
        service.decline(id, currentUser.getId());
    }

    @DeleteMapping("/requests/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //cancelled requests are deleted, so there is nothing to send back
    public void cancel(@PathVariable("id") long id) {
        service.cancel(id, currentUser.getId());
    }

    @GetMapping("/requests")
    public PendingRequestsResponse pending() {
        long me = currentUser.getId();
        List<SwapRequest> pending = service.pendingRequests(me);
        Map<Long, String> names = namesFor(pending);

        List<SwapRequestResponse> incoming = pending.stream()
                .filter(r -> r.getReceiverId() == me)
                .map(r -> toResponse(r, names))
                .toList();
        List<SwapRequestResponse> outgoing = pending.stream()
                .filter(r -> r.getSenderId() == me)
                .map(r -> toResponse(r, names))
                .toList();

        return new PendingRequestsResponse(incoming, outgoing);
    }

    @GetMapping("/connections")
    public List<ConnectionResponse> connections() {
        long me = currentUser.getId();
        List<Long> friendIds = service.connections(me).stream()
                .map(r -> r.otherUser(me))
                .toList();
        Map<Long, String> names = displayNames.displayNamesOf(friendIds);
        return friendIds.stream()
                .map(id -> new ConnectionResponse(id, names.get(id)))
                .toList();
    }
}
