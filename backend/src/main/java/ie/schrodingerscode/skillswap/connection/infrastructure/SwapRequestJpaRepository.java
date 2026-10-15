package ie.schrodingerscode.skillswap.connection.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data writes the SQL for these methods. Only JpaSwapRequestRepository uses it,
 * the rest of the app goes through SwapRequestRepository.
 */
public interface SwapRequestJpaRepository extends JpaRepository<SwapRequestEntity, Long> {

    //a request between the two users, whichever of them sent it
    @Query("""
            SELECT r FROM SwapRequestEntity r
            WHERE (r.senderId = :userA AND r.receiverId = :userB)
               OR (r.senderId = :userB AND r.receiverId = :userA)
            """)
    Optional<SwapRequestEntity> findByUsers(@Param("userA") long userA, @Param("userB") long userB);

    List<SwapRequestEntity> findBySenderIdOrReceiverId(long senderId, long receiverId);
}
