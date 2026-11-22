package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;

/**
 * The real {@link MessageRepository}, it stores messages in the {@code messages} table through
 * {@link MessageJpaRepository}.
 *
 * <p>Spring Data works with {@link MessageEntity}, but the rest of the app works with the domain
 * {@link Message}. This class translates between the two, so JPA code stays in this package.
 */
@Repository
public class JpaMessageRepository implements MessageRepository {

    private final MessageJpaRepository jpa;

    public JpaMessageRepository(MessageJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Message save(Message message) {
        return jpa.save(MessageEntity.from(message)).toDomain();
    }

    @Override
    public List<Message> findConversation(long userA, long userB) {
        return jpa.findConversation(userA, userB).stream().map(MessageEntity::toDomain).toList();
    }

    @Override
    public List<Message> findLatestPerConversation(long userId) {
        return jpa.findLatestPerConversation(userId).stream().map(MessageEntity::toDomain).toList();
    }
}
