/**
 * Chat between connections: sending messages, loading a conversation and the chat list, and pushing
 * new messages live over WebSocket.
 *
 * <p>The package is split into layers. {@code domain} depends on nothing else, {@code application} uses
 * {@code domain}, and {@code infrastructure} and {@code web} plug into those two:
 * <ul>
 *   <li>{@code domain}: {@code Message}, the {@code MessageText} rules, the {@code MessageSent} event and
 *       the {@code MessageRepository} interface. No Spring or database code here.</li>
 *   <li>{@code application}: the use cases ({@code SendMessage}, {@code GetMessageHistory},
 *       {@code ListChats}) that the controllers call.</li>
 *   <li>{@code infrastructure}: the JPA code that saves messages to the {@code messages} table.</li>
 *   <li>{@code web}: the REST controllers, the JSON request and response records, and
 *       {@code PushMessageOnSend}, which pushes each saved message over WebSocket.</li>
 * </ul>
 *
 * <p>Only users who are connected can message each other or read each other's history. That check
 * is done in the application layer through {@code ConnectionChecker} from the connection feature.
 */
package ie.schrodingerscode.skillswap.chat;
