package ie.schrodingerscode.skillswap.profile.domain;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ProfileRepository {
    Optional<Profile> findById(long userId);
    void save(Profile profile);

    // userId -> display name, loaded with one query. Ids without a profile are left out
    Map<Long, String> displayNamesOf(Collection<Long> userIds);
}
