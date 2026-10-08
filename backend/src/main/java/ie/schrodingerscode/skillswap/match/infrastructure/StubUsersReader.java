package ie.schrodingerscode.skillswap.match.infrastructure;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.match.application.SkillSwapUsersReader;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

// JUST A STUB
// TODO: Replace with the actual implementation that will take info about users from the database
@Component
public class StubUsersReader implements SkillSwapUsersReader {
    @Override
    public List<SkillSwapUser> findAll() {
        // Structure of SkillSwapUser: name, learn, teach
        return List.of(
                new SkillSwapUser(1, "Ulven", Set.of("Guitar", "Spanish", "French", "Python"),
                        Set.of("Piano", "Japanese", "JavaScript", "Java")),
                new SkillSwapUser(2, "Jane", Set.of("JavaScript"), Set.of("Guitar", "Spanish")), // two way overlap
                                                                                                 // with
                // Ulven
                new SkillSwapUser(3, "Mike", Set.of("Piano", "Japanese"), Set.of("Guitar", "Spanish", "Yoga")), // two
                // way
                // overlap
                // with
                // Ulven
                new SkillSwapUser(4, "Jessica", Set.of("Japanese"), Set.of("SQL")), // one way overlap with Ulven
                new SkillSwapUser(5, "Doe", Set.of("Python"), Set.of("Cooking, Piano"))); // no overlap with Ulven
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