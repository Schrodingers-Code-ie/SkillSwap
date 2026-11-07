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

    @Query(value = """
                SELECT DISTINCT ON (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)) *
                FROM messages
                WHERE sender_id = :me OR receiver_id = :me
                ORDER BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id), sent_at DESC, id DESC
            """, nativeQuery = true)

    List<MessageEntity> findLatestPerConversation(@Param("me") long me);
}
