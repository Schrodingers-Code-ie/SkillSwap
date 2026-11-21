package ie.schrodingerscode.skillswap.auth.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

/**
 * The real {@link UserRepository}, it stores users in the {@code users} table through {@link UserJpa}.
 *
 * <p>Spring Data works with {@link UserEntity}, but the rest of the app works with the domain
 * {@link User}. This class translates between the two, so JPA code stays in this package.
 */
@Repository
public class JpaUserRepository implements UserRepository {
    private final UserJpa userJpa;

    public JpaUserRepository(UserJpa userJpa) {
        this.userJpa = userJpa;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpa.existsByEmail(email.value());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpa.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<User> findById(long id) {
        return userJpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpa.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        // Returns the saved copy because the database fills in the new ID
        UserEntity saved = userJpa.save(toEntity(user));
        return toDomain(saved);
    }

    private UserEntity toEntity(User user) {
        UserEntity e = new UserEntity();
        e.setId(user.getId());
        e.setEmail(user.getEmail().value());
        e.setUsername(user.getUsername());
        e.setPasswordHash(user.getPasswordHash());
        e.setCreatedAt(user.getCreatedAt());
        return e;
    }

    private User toDomain(UserEntity e) {
        return User.restore(e.getId(), new Email(e.getEmail()), e.getUsername(), e.getPasswordHash(), e.getCreatedAt());
    }

}
