package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA mapping of one row in the {@code messages} table (created by the V3 migration).
 *
 * <p>Only used inside this package; {@link JpaMessageRepository} turns it into a domain {@link Message}
 * and back. The text column is {@code TEXT}: the 2000-character limit is enforced by
 * {@link MessageText}, not by the database.
 */
@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private long senderId;

    @Column(name = "receiver_id", nullable = false)
    private long receiverId;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    /** Needed by JPA. */
    protected MessageEntity() {
    }

    /**
     * Builds an entity from a domain message that is not saved yet (the id is left for the database).
     * {@code sentAt} is cut to microseconds, because Postgres stores timestamps with microsecond
     * precision, so the saved message equals the one loaded back later.
     */
    static MessageEntity from(Message m) {
        MessageEntity e = new MessageEntity();
        e.senderId = m.senderId();
        e.receiverId = m.receiverId();
        e.text = m.text().value();
        e.sentAt = m.sentAt().truncatedTo(ChronoUnit.MICROS);
        return e;
    }

    /** Turns this row back into a domain message. */
    Message toDomain() {
        return new Message(id, senderId, receiverId, new MessageText(text), sentAt);
    }
}
