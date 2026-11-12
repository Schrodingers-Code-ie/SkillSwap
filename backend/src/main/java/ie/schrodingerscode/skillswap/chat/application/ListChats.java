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
     * @param userId id of the logged-in user
     * @return one entry per connection, sorted for the chat list
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
