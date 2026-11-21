package ie.schrodingerscode.skillswap.auth.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link UserEntity}.
 *
 * <p>Spring writes the SQL for these methods from their names, for example
 * {@code existsByUsernameIgnoreCase} becomes a case-insensitive check on the username column.
 * {@code save} and {@code findById} come from {@link JpaRepository}.
 */
public interface UserJpa extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByEmail(String email);
}
