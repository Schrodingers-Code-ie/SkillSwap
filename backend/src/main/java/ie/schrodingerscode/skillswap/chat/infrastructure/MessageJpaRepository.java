package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {

    @Query(value = """
                SELECT * FROM messages
                WHERE LEAST(sender_id, receiver_id) = LEAST(:a, :b) AND GREATEST(sender_id, receiver_id) = GREATEST(:a, :b)
                ORDER BY sent_at, id
            """, nativeQuery = true)

    List<MessageEntity> findConversation(@Param("a") long a, @Param("b") long b);
}
