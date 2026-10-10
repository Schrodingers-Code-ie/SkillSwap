package ie.schrodingerscode.skillswap.connection.application;

import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;
import ie.schrodingerscode.skillswap.connection.domain.exception.DuplicateRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.RequestNotFoundException;

/**
 * Loads requests, asks the SwapRequest to do the action, and saves the result.
 * The rules themselves (who can accept, what state is allowed) are inside SwapRequest, not here.
 */
@Service
public class SwapRequestService implements ConnectionChecker {

    private final SwapRequestRepository repository;

    public SwapRequestService(SwapRequestRepository repository) {
        this.repository = repository;
    }

    //synchronized so two requests sent at the same moment can't both pass the duplicate check
    public synchronized SwapRequest send(long senderId, long receiverId) {
        if (repository.findByPair(UserPair.of(senderId, receiverId)).isPresent()) {
            throw new DuplicateRequestException();
        }
        SwapRequest request = SwapRequest.create(repository.nextId(), senderId, receiverId);
        repository.save(request);
        return request;
    }

    public SwapRequest accept(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.accept(userId);
        repository.save(request);
        return request;
    }

    public void decline(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.decline(userId);
        repository.delete(request);
    }

    //pending requests I sent or received
    public List<SwapRequest> pendingRequests(long userId) {
        return repository.findAllInvolving(userId).stream()
                .filter(SwapRequest::isPending)
                .toList();
    }

    //accepted requests, i.e. the people I'm connected with
    public List<SwapRequest> connections(long userId) {
        return repository.findAllInvolving(userId).stream()
                .filter(SwapRequest::isAccepted)
                .toList();
    }

    @Override
    public boolean areConnected(long userA, long userB) {
        return repository.findByPair(UserPair.of(userA, userB))
                .map(SwapRequest::isAccepted)
                .orElse(false);
    }

    private SwapRequest findRequest(long requestId) {
        return repository.findById(requestId).orElseThrow(RequestNotFoundException::new);
    }
}
