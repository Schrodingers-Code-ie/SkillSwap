package ie.schrodingerscode.skillswap.connection.web.dto;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;

/**
 * What we send back for one request, so the domain object itself never goes out as JSON.
 * The display names are null if the user has no profile.
 *
 * @param id the request id
 * @param senderId who sent it
 * @param receiverId who it was sent to
 * @param status PENDING or ACCEPTED
 * @param senderDisplayName the sender's display name
 * @param receiverDisplayName the receiver's display name
 */
public record SwapRequestResponse(long id, long senderId, long receiverId, String status,
        String senderDisplayName, String receiverDisplayName) {

    public static SwapRequestResponse from(SwapRequest request, String senderDisplayName, String receiverDisplayName) {
        return new SwapRequestResponse(
                request.getId(),
                request.getSenderId(),
                request.getReceiverId(),
                request.getStatus().name(),
                senderDisplayName,
                receiverDisplayName);
    }
}
