package ie.schrodingerscode.skillswap.auth.domain;

import java.util.Optional;

/**
 * Saves and finds users.
 *
 * <p>The interface is in the domain so the use cases do not depend on JPA. The real version is
 * {@code JpaUserRepository}, and the unit tests use {@code FakeUserRepository} instead of a database.
 */
public interface UserRepository {
    /**
     * @param email the email to look for
     * @return true if a user already has this email
     */
    boolean existsByEmail(Email email);

    /**
     * @param username the username to look for, case is ignored
     * @return true if a user already has this username
     */
    boolean existsByUsername(String username);

    /**
     * @param email the email to look for
     * @return the user, or empty if no account has this email
     */
    Optional<User> findByEmail(Email email);

    /**
     * @param id the user ID
     * @return the user, or empty if there is no user with this ID
     */
    Optional<User> findById(long id);

    /**
     * Saves a new user.
     *
     * @param user the user to save, with a null ID
     * @return the saved user with the ID the database gave it
     */
    User save(User user);
}
