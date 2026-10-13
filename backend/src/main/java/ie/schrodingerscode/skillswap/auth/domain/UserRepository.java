package ie.schrodingerscode.skillswap.auth.domain;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(Email email);

    Optional<User> findByEmail(Email email);

    User save(User user);
}
