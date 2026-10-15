package ie.schrodingerscode.skillswap.auth.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

// This one is used in RegisterUserTests instead of InMemoryUserRepository, which was deleted
public class FakeUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap();
    private long nextId = 1;

    @Override
    public boolean existsByEmail(Email email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return users.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        User saved = User.restore(nextId++, user.getEmail(), user.getUsername(), user.getPasswordHash(),
                user.getCreatedAt());
        users.put(saved.getId(), saved);
        return saved;
    }
}
