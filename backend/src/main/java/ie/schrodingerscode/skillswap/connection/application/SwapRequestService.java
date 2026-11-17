package ie.schrodingerscode.skillswap.connection.application;

import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.application.UserChecker;
import ie.schrodingerscode.skillswap.block.application.BlockChecker;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;
import ie.schrodingerscode.skillswap.connection.domain.exception.BlockedRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.DuplicateRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.ReceiverNotFoundException;
import ie.schrodingerscode.skillswap.connection.domain.exception.RequestNotFoundException;
import ie.schrodingerscode.skillswap.match.application.SharedSkills;
import ie.schrodingerscode.skillswap.match.application.SkillOverlap;

/**
 * Loads requests, asks the SwapRequest to do the action, and saves the result.
 * The rules themselves (who can accept, what state is allowed) are inside SwapRequest, not here.
 */
@Service
public class SwapRequestService implements ConnectionChecker, ConnectionLister, ConnectionRemover {

    private final SwapRequestRepository repository;
    private final UserChecker users;
    private final BlockChecker blocks;
    private final SharedSkills sharedSkills;

    public SwapRequestService(SwapRequestRepository repository, UserChecker users, BlockChecker blocks, SharedSkills sharedSkills) {
        this.repository = repository;
        this.users = users;
        this.blocks = blocks;
        this.sharedSkills = sharedSkills;
    }

    //synchronized so two requests sent at the same moment can't both pass the duplicate check
    //(the unique index in the database is a backstop for this too)
    public synchronized SwapRequest send(long senderId, long receiverId) {
        //otherwise the foreign key in the database rejects it and the API gives a 500
        if (!users.exists(receiverId)) {
            throw new ReceiverNotFoundException();
        }
        //works both ways: you can't request someone you blocked, or someone who blocked you
        if (blocks.isBlockedEitherWay(senderId, receiverId)) {
            throw new BlockedRequestException();
        }
        if (repository.findByPair(UserPair.of(senderId, receiverId)).isPresent()) {
            throw new DuplicateRequestException();
        }
        return repository.save(SwapRequest.create(senderId, receiverId));
    }

    public SwapRequest accept(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.accept(userId);
        return repository.save(request);
    }

    public void decline(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.decline(userId);
        repository.delete(request);
    }

    public void cancel(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.cancel(userId);
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

    @Override
    public List<Long> connectionIdsOf(long userId) {
        return connections(userId).stream()
                .map(request -> request.otherUser(userId))
                .toList();
    }

    @Override
    public void removeBetween(long userA, long userB) {
        repository.findByPair(UserPair.of(userA, userB)).ifPresent(repository::delete);
    }
  
    //friends with the skills we can swap
    public List<SkillOverlap> friendsWithSkills(long userId) {
        return sharedSkills.between(userId, connectionIdsOf(userId));
    }

    private SwapRequest findRequest(long requestId) {
        return repository.findById(requestId).orElseThrow(RequestNotFoundException::new);
    }
}
