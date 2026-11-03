package ie.schrodingerscode.skillswap.connection.web.dto;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;

//what we send back for one request, so the domain object itself never goes out as JSON
//the usernames are null if the user can't be found
public record SwapRequestResponse(long id, long senderId, long receiverId, String status,
        String senderUsername, String receiverUsername) {

    public static SwapRequestResponse from(SwapRequest request, String senderUsername, String receiverUsername) {
        return new SwapRequestResponse(
                request.getId(),
                request.getSenderId(),
                request.getReceiverId(),
                request.getStatus().name(),
                senderUsername,
                receiverUsername);
    }
}
