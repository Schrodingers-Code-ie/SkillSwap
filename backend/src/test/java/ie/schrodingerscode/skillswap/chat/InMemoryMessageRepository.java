package ie.schrodingerscode.skillswap.chat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    private boolean isBetween(Message m, long a, long b) {
        return (m.senderId() == a && m.receiverId() == b) || (m.senderId() == b && m.receiverId() == a);
    }
}
