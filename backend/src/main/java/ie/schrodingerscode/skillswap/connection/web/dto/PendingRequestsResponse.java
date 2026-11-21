package ie.schrodingerscode.skillswap.connection.web.dto;

import java.util.List;

/**
 * GET /api/requests: requests sent to me and requests I sent, both still pending.
 *
 * @param incoming requests other people sent to me
 * @param outgoing requests I sent
 */
public record PendingRequestsResponse(List<SwapRequestResponse> incoming, List<SwapRequestResponse> outgoing) {
}
