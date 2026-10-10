package ie.schrodingerscode.skillswap.connection.domain;

import ie.schrodingerscode.skillswap.connection.domain.exception.InvalidRequestStateException;
import ie.schrodingerscode.skillswap.connection.domain.exception.NotRequestReceiverException;
import ie.schrodingerscode.skillswap.connection.domain.exception.SelfRequestException;

/**
 * A swap request from one user to another (the aggregate).
 *
 * All the rules about what can happen to a request live here, in accept() and decline(),
 * not in the controller or service. The service just loads the request, calls the method and saves it.
 */
public class SwapRequest {

    private final long id;
    private final long senderId;
    private final long receiverId;
    private RequestStatus status;

    private SwapRequest(long id, long senderId, long receiverId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = RequestStatus.PENDING;
    }

    //the only way to make a new request, so a request to yourself can never exist
    public static SwapRequest create(long id, long senderId, long receiverId) {
        if (senderId == receiverId) {
            throw new SelfRequestException();
        }
        return new SwapRequest(id, senderId, receiverId);
    }

    public void accept(long userId) {
        checkIsReceiver(userId); //check this first, so a stranger doesn't find out the request's status
        if (status != RequestStatus.PENDING) {
            throw new InvalidRequestStateException("Request has already been accepted");
        }
        status = RequestStatus.ACCEPTED;
    }

    //the service deletes the request after this, we only check that declining is allowed
    public void decline(long userId) {
        checkIsReceiver(userId);
        if (status != RequestStatus.PENDING) {
            //declining an accepted request would really be "remove connection", which is a different feature
            throw new InvalidRequestStateException("Only pending requests can be declined");
        }
    }

    private void checkIsReceiver(long userId) {
        if (userId != receiverId) {
            throw new NotRequestReceiverException();
        }
    }

    public UserPair pair() {
        return UserPair.of(senderId, receiverId);
    }

    public boolean involves(long userId) {
        return userId == senderId || userId == receiverId;
    }

    //for a connection, gives the other person (e.g. user 1 asks, gets user 2 back)
    public long otherUser(long userId) {
        return userId == senderId ? receiverId : senderId;
    }

    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == RequestStatus.ACCEPTED;
    }

    public long getId() {
        return id;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public RequestStatus getStatus() {
        return status;
    }
}
