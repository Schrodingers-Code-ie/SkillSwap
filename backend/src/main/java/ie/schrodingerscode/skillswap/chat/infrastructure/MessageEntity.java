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

    protected MessageEntity() {
    }

    static MessageEntity from(Message m) {
        MessageEntity e = new MessageEntity();
        e.senderId = m.senderId();
        e.receiverId = m.receiverId();
        e.text = m.text().value();
        e.sentAt = m.sentAt().truncatedTo(ChronoUnit.MICROS);
        return e;
    }

    Message toDomain() {
        return new Message(id, senderId, receiverId, new MessageText(text), sentAt);
    }
}
