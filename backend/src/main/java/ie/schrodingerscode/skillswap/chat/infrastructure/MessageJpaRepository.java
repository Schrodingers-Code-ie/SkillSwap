package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data repository for {@link MessageEntity}. Only used by {@link JpaMessageRepository}.
 *
 * <p>Both queries treat a conversation as the pair (smaller id, bigger id) using {@code LEAST} and
 * {@code GREATEST}, so messages in both directions belong to the same conversation. They are native
 * Postgres queries because {@code DISTINCT ON} is Postgres-only.
 */
public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {

    /**
     * Every message between users a and b, in both directions, oldest first. Messages sent at the same
     * time are ordered by id.
     */
    @Query(value = """
                SELECT * FROM messages
                WHERE LEAST(sender_id, receiver_id) = LEAST(:a, :b) AND GREATEST(sender_id, receiver_id) = GREATEST(:a, :b)
                ORDER BY sent_at, id
            """, nativeQuery = true)

    List<MessageEntity> findConversation(@Param("a") long a, @Param("b") long b);

    /**
     * The newest message of every conversation user {@code me} is in: {@code DISTINCT ON} keeps the
     * first row of each pair, and the {@code ORDER BY} puts the newest (then highest id) first.
     */
    @Query(value = """
                SELECT DISTINCT ON (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)) *
                FROM messages
                WHERE sender_id = :me OR receiver_id = :me
                ORDER BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id), sent_at DESC, id DESC
            """, nativeQuery = true)

    List<MessageEntity> findLatestPerConversation(@Param("me") long me);
}
