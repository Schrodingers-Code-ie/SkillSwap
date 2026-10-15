package ie.schrodingerscode.skillswap.auth.domain;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(Email email);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(Email email);

    Optional<User> findById(long id);

    User save(User user);
}
