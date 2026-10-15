package ie.schrodingerscode.skillswap.profile.infrastructure;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProfileRepository implements ProfileRepository {

    private final Map<Long, Profile> profiles = new ConcurrentHashMap<>();

    public InMemoryProfileRepository() {
        Profile alice = new Profile(1, "Alice", "Guitarist who wants to get into coding.");
        alice.addTeachSkill(1, Proficiency.ADVANCED); // Guitar
        alice.addTeachSkill(6, Proficiency.INTERMEDIATE); // Spanish
        alice.addLearnSkill(12); // Python
        alice.addLearnSkill(21); // Yoga
        save(alice);

        Profile bob = new Profile(2, "Bob", "Developer who loves baking.");
        bob.addTeachSkill(12, Proficiency.ADVANCED); // Python
        bob.addTeachSkill(16, Proficiency.BEGINNER); // Baking
        bob.addLearnSkill(1); // Guitar
        bob.addLearnSkill(6); // Spanish
        save(bob);

        Profile carol = new Profile(3, "Carol", "Yoga teacher learning to code.");
        carol.addTeachSkill(21, Proficiency.ADVANCED); // Yoga
        carol.addTeachSkill(11, Proficiency.INTERMEDIATE); // Java
        carol.addLearnSkill(16); // Baking
        carol.addLearnSkill(12); // Python
        save(carol);
    }

    @Override
    public Optional<Profile> findById(long userId) {
        return Optional.ofNullable(profiles.get(userId));
    }

    @Override
    public void save(Profile profile) {
        profiles.put(profile.getUserId(), profile);
    }
}
