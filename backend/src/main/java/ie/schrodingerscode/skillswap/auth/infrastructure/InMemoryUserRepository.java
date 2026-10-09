package ie.schrodingerscode.skillswap.auth.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Email, User> usersByEmail = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public boolean existsByEmail(Email email) {
        return usersByEmail.containsKey(email);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public User save(User user) {
        User saved = user.getId() == null ? user.withId(nextId.getAndIncrement()) : user;
        usersByEmail.put(saved.getEmail(), saved);
        return saved;
    }
}
