package ie.schrodingerscode.skillswap.profile.domain;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Where profiles are stored. The domain only knows this interface;
 * JpaProfileRepository is the real one and FakeProfileRepository is used in unit tests.
 */
public interface ProfileRepository {

    /**
     * Loads a user's profile with both skill lists.
     *
     * @param userId the user whose profile to load
     * @return the profile, or empty if the user has none
     */
    Optional<Profile> findById(long userId);

    /**
     * Creates the profile or replaces the stored one, including both skill lists.
     *
     * @param profile the profile to store
     */
    void save(Profile profile);

    /**
     * User id to display name, loaded with one query. Ids without a profile are left out.
     *
     * @param userIds the users to look up
     * @return user id to display name
     */
    Map<Long, String> displayNamesOf(Collection<Long> userIds);
}
