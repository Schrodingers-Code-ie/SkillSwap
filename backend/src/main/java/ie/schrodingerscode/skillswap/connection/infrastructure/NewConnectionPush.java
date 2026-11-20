package ie.schrodingerscode.skillswap.connection.infrastructure;

// What PushConnectionOnCreate sends on /user/queue/connections: the new friend
public record NewConnectionPush(long userId, String displayName) {
}
