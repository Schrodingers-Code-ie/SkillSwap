package ie.schrodingerscode.skillswap.connection.infrastructure;

import java.time.Instant;

import ie.schrodingerscode.skillswap.connection.domain.RequestStatus;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * One row of the swap_requests table.
 * Kept separate from SwapRequest so the domain has no JPA annotations (STRUCTURE.md rule 2).
 */
@Entity
@Table(name = "swap_requests")
public class SwapRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Postgres hands out the ids
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private long senderId;

    @Column(name = "receiver_id", nullable = false)
    private long receiverId;

    @Enumerated(EnumType.STRING) //stored as 'PENDING' or 'ACCEPTED', not as a number
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SwapRequestEntity() {
        //JPA needs an empty constructor
    }

    static SwapRequestEntity from(SwapRequest request) {
        SwapRequestEntity entity = new SwapRequestEntity();
        entity.id = request.getId(); //null for a new request, so JPA inserts instead of updating
        entity.senderId = request.getSenderId();
        entity.receiverId = request.getReceiverId();
        entity.status = request.getStatus();
        entity.createdAt = request.getCreatedAt();
        return entity;
    }

    SwapRequest toDomain() {
        return SwapRequest.restore(id, senderId, receiverId, status, createdAt);
    }
}
