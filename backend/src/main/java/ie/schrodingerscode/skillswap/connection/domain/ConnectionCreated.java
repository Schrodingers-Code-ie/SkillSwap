package ie.schrodingerscode.skillswap.connection.domain;

// Published by SwapRequestService when a request is accepted. PushConnectionOnCreate pushes it over the WebSocket.
public record ConnectionCreated(long senderId, long receiverId) {
}
