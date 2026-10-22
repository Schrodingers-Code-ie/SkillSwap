package ie.schrodingerscode.skillswap.match.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.match.application.SkillSwapUsersReader;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

@Component
public class StubUsersReader implements SkillSwapUsersReader {
    private final List<SkillSwapUser> fakeUsers = List.of(
            new SkillSwapUser(1, "Ulven", Set.of("Guitar", "Spanish", "French", "Python"),
                    Set.of("Piano", "Japanese", "JavaScript", "Java")),
            new SkillSwapUser(2, "Jane", Set.of("JavaScript"), Set.of("Guitar", "Spanish")),
            new SkillSwapUser(3, "Mike", Set.of("Piano", "Japanese"), Set.of("Guitar", "Spanish", "Yoga")),
            new SkillSwapUser(4, "Jessica", Set.of("Japanese"), Set.of("SQL")),
            new SkillSwapUser(5, "Doe", Set.of("Python"), Set.of("Cooking", "Piano")));

    @Override
    public Optional<SkillSwapUser> findById(long id) {
        for (SkillSwapUser user : fakeUsers) {
            if (user.id() == id)
                return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<SkillSwapUser> findCandidatesFor(long meId) {
        List<SkillSwapUser> candidates = new ArrayList<>();

        for (SkillSwapUser user : fakeUsers) {
            if (user.id() != meId)
                candidates.add(user);
        }
        return candidates;
    }
}

// Predictions for matching:
// Ulven and Jane:
// skills[Ulven]CanTeach[Jane] = 1 (JavaScript)
// skills[Jane]CanTeach[Ulven] = 2 (Guitar, Spanish)
// matchingScore = 1 + 2 = 3

// Ulven and Mike:
// skills[Ulven]CanTeach[Mike] = 2 (Piano, Japanese)
// skills[Mike]CanTeach[Ulven] = 2 (Guitar, Spanish)
// matchingScore = 2 + 2 = 4

// Ulven and Jessica:
// skills[Ulven]CanTeach[Jessica] = 1 (Japanese)
// skills[Jessica]CanTeach[Ulven] = 0
// matchingScore = 1 + 0 = 1

// Ulven and Doe:
// no match for learning or teaching skills
// matchingScore = 0
