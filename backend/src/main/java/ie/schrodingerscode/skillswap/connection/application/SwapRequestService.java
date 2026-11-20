package ie.schrodingerscode.skillswap.connection.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import ie.schrodingerscode.skillswap.auth.application.UserChecker;
import ie.schrodingerscode.skillswap.block.application.BlockChecker;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;
import ie.schrodingerscode.skillswap.connection.domain.ConnectionCreated;
import ie.schrodingerscode.skillswap.connection.domain.exception.BlockedRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.DuplicateRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.InvalidRequestStateException;
import ie.schrodingerscode.skillswap.connection.domain.exception.NotRequestReceiverException;
import ie.schrodingerscode.skillswap.connection.domain.exception.NotRequestSenderException;
import ie.schrodingerscode.skillswap.connection.domain.exception.ReceiverNotFoundException;
import ie.schrodingerscode.skillswap.connection.domain.exception.RequestNotFoundException;
import ie.schrodingerscode.skillswap.connection.domain.exception.SelfRequestException;
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
    private final ApplicationEventPublisher events;
    private final BlockChecker blocks;
    private final SharedSkills sharedSkills;

    public SwapRequestService(SwapRequestRepository repository, UserChecker users, BlockChecker blocks,
            SharedSkills sharedSkills, ApplicationEventPublisher events) {
        this.repository = repository;
        this.users = users;
        this.blocks = blocks;
        this.sharedSkills = sharedSkills;
        this.events = events;
    }

    /**
     * Sends a new pending request.
     * Synchronized so two requests sent at the same moment can't both pass the duplicate check
     * (the unique index in the database is a backstop for this too).
     *
     * @param senderId the logged in user
     * @param receiverId who the request goes to
     * @return the saved request, with its id
     * @throws ReceiverNotFoundException if the receiver doesn't exist
     * @throws BlockedRequestException if one of them blocked the other
     * @throws DuplicateRequestException if there is already a request between them
     * @throws SelfRequestException if sending to yourself
     */
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

    /**
     * Accepts a request and publishes ConnectionCreated, so both users get the new friend live.
     *
     * @param requestId the request to accept
     * @param userId the logged in user, has to be the receiver
     * @return the accepted request
     * @throws RequestNotFoundException if there is no request with this id
     * @throws NotRequestReceiverException if the user isn't the receiver
     * @throws InvalidRequestStateException if it's already accepted
     */
    @Transactional
    public SwapRequest accept(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.accept(userId);
        SwapRequest saved = repository.save(request);
        events.publishEvent(new ConnectionCreated(saved.getSenderId(), saved.getReceiverId()));
        return saved;
    }

    /**
     * Declines a request, it gets deleted so the sender can send again later.
     *
     * @param requestId the request to decline
     * @param userId the logged in user, has to be the receiver
     * @throws RequestNotFoundException if there is no request with this id
     * @throws NotRequestReceiverException if the user isn't the receiver
     * @throws InvalidRequestStateException if it's not pending
     */
    public void decline(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.decline(userId);
        repository.delete(request);
    }

    /**
     * Cancels a request I sent, it gets deleted.
     *
     * @param requestId the request to cancel
     * @param userId the logged in user, has to be the sender
     * @throws RequestNotFoundException if there is no request with this id
     * @throws NotRequestSenderException if the user isn't the sender
     * @throws InvalidRequestStateException if it's not pending
     */
    public void cancel(long requestId, long userId) {
        SwapRequest request = findRequest(requestId);
        request.cancel(userId);
        repository.delete(request);
    }

    /**
     * Pending requests I sent or received.
     *
     * @param userId the logged in user
     * @return incoming and outgoing pending requests together
     */
    public List<SwapRequest> pendingRequests(long userId) {
        return repository.findAllInvolving(userId).stream()
                .filter(SwapRequest::isPending)
                .toList();
    }

    /**
     * Accepted requests, i.e. the people I'm connected with.
     *
     * @param userId the logged in user
     * @return the accepted requests
     */
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

    /**
     * Friends with the skills we can swap.
     *
     * @param userId the logged in user
     * @return one entry per friend, with their username and the skills on both sides
     */
    public List<SkillOverlap> friendsWithSkills(long userId) {
        return sharedSkills.between(userId, connectionIdsOf(userId));
    }

    private SwapRequest findRequest(long requestId) {
        return repository.findById(requestId).orElseThrow(RequestNotFoundException::new);
    }
}
