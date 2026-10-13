package ie.schrodingerscode.skillswap.connection.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//what the client sends to POST /api/requests, e.g. { "receiverId": 2 }
public record SendRequestBody(@NotNull @Positive Long receiverId) {
}
