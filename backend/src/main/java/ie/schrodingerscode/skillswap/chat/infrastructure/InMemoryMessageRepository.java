package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;

/**
 * Keeps messages in a list in memory. Everything is lost when the app restarts.
 * Replace with a JPA implementation later: only this class changes.
 * The annotation goes here, not on the interface, so the domain stays
 * Spring-free.
 */
@Repository
public class InMemoryMessageRepository implements MessageRepository {
    private final List<Message> messages = new ArrayList<>();
    /**
     * Generates a unique ID for each saved message, starting at 1 with atomic
     * protection.
     * nextId cannot increase at the same time assigning the same id.
     */
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Assigns an ID to a message, stores it, and returns the saved message.
     * withId() creates a new Message because Message is immutable.
     * synchronized prevents concurrent requests from modifying the list
     * simultaneously.
     * 
     * @param message message to save
     * @return saved message with its assigned ID
     */
    @Override
    public synchronized Message save(Message message) {
        Message saved = message.withId(nextId.getAndIncrement());
        messages.add(saved);
        return saved;
    }

    /**
     * Retrieves all messages exchanged between two users.
     * Includes messages sent in either direction and sorts them by timestamp,
     * using the message id to break ties when timestamps are equal.
     * 
     * @param userA ID of the first user
     * @param userB ID of the second user
     * @return conversation messages, oldest first
     */
    @Override
    public synchronized List<Message> findConversation(long userA, long userB) {
        return messages.stream().filter(m -> isBetween(m, userA, userB))
                .sorted(Comparator.comparing((Message m) -> m.sentAt()).thenComparing(m -> m.id())).toList();
    }

    /**
     * Checks whether a message was exchanged between the two specified users,
     * regardless of who sent it.
     *
     * @param m message to check
     * @param a id of the first user
     * @param b id of the second user
     * @return true if the message belongs to their conversation
     */
    private boolean isBetween(Message m, long a, long b) {
        return (m.senderId() == a && m.receiverId() == b) || (m.senderId() == b && m.receiverId() == a);
    }
}
