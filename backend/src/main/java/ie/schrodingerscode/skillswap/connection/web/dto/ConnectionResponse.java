package ie.schrodingerscode.skillswap.connection.web.dto;

//one person I'm connected with, displayName is null if they have no profile
public record ConnectionResponse(long userId, String displayName) {
}
