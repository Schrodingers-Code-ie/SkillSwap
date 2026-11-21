package ie.schrodingerscode.skillswap.chat.application;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;
import ie.schrodingerscode.skillswap.connection.application.ConnectionLister;
import ie.schrodingerscode.skillswap.profile.application.DisplayNames;

/**
 * Use case that builds the chat list shown on the left of the Chat page.
 *
 * <p>Guarantees one entry per current connection, and only for connections: messages with someone
 * who is no longer a connection are left out. The order is fixed here, not in the frontend:
 * <ol>
 *   <li>chats with messages, newest last message first (for the same time, the higher id first);</li>
 *   <li>then connections without messages, by display name A to Z ignoring case, with missing names
 *       last.</li>
 * </ol>
 */
@Service
public class ListChats {

    private static final Comparator<String> BY_NAME = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

    private final MessageRepository messages;
    private final ConnectionLister connections;
    private final DisplayNames displayNames;

    /**
     * @param messages     repository used to load the last messages
     * @param connections  gives the ids of the user's connections
     * @param displayNames looks up display names
     */
    public ListChats(MessageRepository messages, ConnectionLister connections, DisplayNames displayNames) {
        this.messages = messages;
        this.connections = connections;
        this.displayNames = displayNames;
    }

    /**
     * Builds the chat list for one user.
     *
     * @param userId id of the logged-in user
     * @return one entry per connection, in chat list order; empty if the user has no connections
     */
    public List<ChatSummary> execute(long userId) {
        List<Long> friendIds = connections.connectionIdsOf(userId);
        Map<Long, String> names = displayNames.displayNamesOf(friendIds);
        Map<Long, Message> lastMessageWith = messages.findLatestPerConversation(userId).stream()
                .collect(Collectors.toMap(m -> m.otherUser(userId), m -> m));

        return friendIds.stream()
                .map(id -> new ChatSummary(id, names.get(id), lastMessageWith.get(id)))
                .sorted(ListChats::chatListOrder)
                .toList();
    }

    /**
     * Comparator for the chat list order described on the class.
     */
    private static int chatListOrder(ChatSummary x, ChatSummary y) {
        if (x.hasMessages() && y.hasMessages()) {
            Message a = x.lastMessage();
            Message b = y.lastMessage();
            int byTime = b.sentAt().compareTo(a.sentAt());
            if (byTime != 0) {
                return byTime;
            }
            return Long.compare(b.id(), a.id());
        }
        if (x.hasMessages()) {
            return -1;
        }
        if (y.hasMessages()) {
            return 1;
        }
        return BY_NAME.compare(x.displayName(), y.displayName());
    }
}
