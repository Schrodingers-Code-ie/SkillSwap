package ie.schrodingerscode.skillswap.profile;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeProfileRepository implements ProfileRepository {

    private final Map<Long, Profile> store = new HashMap<>();

    @Override
    public Optional<Profile> findById(long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public void save(Profile profile) {
        store.put(profile.getUserId(), profile);
    }
}
