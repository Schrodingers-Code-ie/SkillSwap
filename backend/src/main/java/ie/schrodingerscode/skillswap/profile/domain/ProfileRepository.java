package ie.schrodingerscode.skillswap.profile.domain;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Where profiles are stored. The domain only knows this interface;
 * JpaProfileRepository is the real one and FakeProfileRepository is used in unit tests.
 */

public interface ProfileRepository {
    Optional<Profile> findById(long userId);
    void save(Profile profile);

    /**
     * User id to display name, loaded with one query. Ids without a profile are left out.
     */
    Map<Long, String> displayNamesOf(Collection<Long> userIds);
}
