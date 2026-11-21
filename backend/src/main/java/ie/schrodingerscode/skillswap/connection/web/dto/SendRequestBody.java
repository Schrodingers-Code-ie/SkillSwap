package ie.schrodingerscode.skillswap.connection.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * What the client sends to POST /api/requests, e.g. {@code { "receiverId": 2 }}.
 *
 * @param receiverId who the request goes to
 */
public record SendRequestBody(@NotNull @Positive Long receiverId) {
}
