package ie.schrodingerscode.skillswap.connection.web.dto;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;

//what we send back for one request, so the domain object itself never goes out as JSON
//the display names are null if the user has no profile
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
