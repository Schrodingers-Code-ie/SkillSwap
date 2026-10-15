package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.List;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageRepository;

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
}
