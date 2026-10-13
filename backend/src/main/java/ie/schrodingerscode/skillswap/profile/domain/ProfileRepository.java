package ie.schrodingerscode.skillswap.profile.domain;

import java.util.Optional;

public interface ProfileRepository {
    Optional<Profile> findById(long userId);
    void save(Profile profile);
}