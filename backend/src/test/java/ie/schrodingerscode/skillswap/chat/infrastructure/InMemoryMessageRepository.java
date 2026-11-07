package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;

public class InMemoryMessageRepository implements MessageRepository {
    private final List<Message> messages = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public synchronized Message save(Message message) {
        Message saved = message.withId(nextId.getAndIncrement());
        messages.add(saved);
        return saved;
    }

    @Override
    public synchronized List<Message> findConversation(long userA, long userB) {
        return messages.stream().filter(m -> isBetween(m, userA, userB))
                .sorted(Comparator.comparing((Message m) -> m.sentAt()).thenComparing(m -> m.id())).toList();
    }

    @Override
    public synchronized List<Message> findLatestPerConversation(long userId) {
        Map<Long, Message> latestByOtherUser = messages.stream()
                .filter(m -> m.senderId() == userId || m.receiverId() == userId)
                .collect(Collectors.toMap(
                        m -> m.otherUser(userId),
                        m -> m,
                        BinaryOperator.maxBy(Comparator.comparing(Message::sentAt).thenComparing(Message::id))));
        return List.copyOf(latestByOtherUser.values());
    }

    private boolean isBetween(Message m, long a, long b) {
        return (m.senderId() == a && m.receiverId() == b) || (m.senderId() == b && m.receiverId() == a);
    }
}
