package ie.schrodingerscode.skillswap.connection.web.dto;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;

//what we send back for one request, so the domain object itself never goes out as JSON
public record SwapRequestResponse(long id, long senderId, long receiverId, String status) {

    public static SwapRequestResponse from(SwapRequest request) {
        return new SwapRequestResponse(
                request.getId(),
                request.getSenderId(),
                request.getReceiverId(),
                request.getStatus().name());
    }
}
