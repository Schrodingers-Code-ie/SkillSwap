package ie.schrodingerscode.skillswap.auth.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

// Basically a converted: User <-> UserEntity
// RegisterUser has a method register() and this method calls userRepository.save(). Inside UserRepository we see that save() returns User, and this save() is implement IN HERE by JpaUserRepository (find below) - since this is the only implementation of UserRepository, Spring automatically inserts it into RegisterUser's variable (which is why we deleted InMemoryUserRepository, the old implementation)
// Postgres won't be able to store it - it works ONLY with UserEntity, so something needs to CONVERT User to UserEntity
// Inside UserJpa class, JpaRepository<UserEntity, Long>.save (provided to us by default) returns UserEntity
// These two types are incompatible, so something has to sit in between and convert them - that's JpaUserRepository's work
@Repository
public class JpaUserRepository implements UserRepository {
    private final UserJpa userJpa; // What will TALK TO DATABASE, but it requires UserEntity, not User returned by UserRepository

    public JpaUserRepository(UserJpa userJpa) {
        this.userJpa = userJpa;
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpa.existsByEmail(email.value()); // userJpa is used everywhere for these because we're asking the DATABASE is this user exists
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpa.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<User> findById(long id) {
        Optional<UserEntity> userEntity = userJpa.findById(id); // UserJpa returns UserEntity, because that's what it works with
        // But we need User, not UserEntity, so JpaUserRepository (this class) translates it:
        return userEntity.map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpa.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public User save(User user) { // THIS ONE is used inside RegisterUser, and it returns User
        UserEntity converted = toEntity(user); // Database only works with UserEntity, so converting from User to UserEntity
        UserEntity saved = userJpa.save(converted); // Saving to database - actually runs INSERT INTO users ... (this code is generated automatically by Spring from JpaRepository<UserEntity, Long>)
        return toDomain(saved); // Convert UserEntity BACK to User and return, because this function save() is STILL used inside RegisterUser, and RegisterUser expects User to be returned
    }

    private UserEntity toEntity(User user) { // Converts User to UserEntity, used in the save() method above
        UserEntity e = new UserEntity();
        e.setId(user.getId());
        e.setEmail(user.getEmail().value());
        e.setUsername(user.getUsername());
        e.setPasswordHash(user.getPasswordHash());
        e.setCreatedAt(user.getCreatedAt());
        return e;
    }

    private User toDomain(UserEntity e) { // Just converts UserEntity back to User, used in save() method above
        // This method restore() sits inside User class
        return User.restore(e.getId(), new Email(e.getEmail()), e.getUsername(), e.getPasswordHash(), e.getCreatedAt());
    }

}
