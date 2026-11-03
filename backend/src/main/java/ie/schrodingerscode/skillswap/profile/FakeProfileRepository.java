package ie.schrodingerscode.skillswap.profile;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Map<Long, String> displayNamesOf(Collection<Long> userIds) {
        return userIds.stream()
                .filter(store::containsKey)
                .collect(Collectors.toMap(id -> id, id -> store.get(id).getDisplayName()));
    }
}
