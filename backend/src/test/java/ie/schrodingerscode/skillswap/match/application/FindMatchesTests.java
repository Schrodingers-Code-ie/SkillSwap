package ie.schrodingerscode.skillswap.match.application;

import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.match.application.exception.UserNotFoundException;
import ie.schrodingerscode.skillswap.match.domain.Match;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

public class FindMatchesTests {
    private SkillSwapUser me = new SkillSwapUser(1, "Me", "me123", Set.of("Guitar", "Spanish"),
            Set.of("Piano", "Japanese"));

    private SkillSwapUser strongMatch = new SkillSwapUser(2, "Strong", "StronG12", Set.of("Piano", "Japanese"),
            Set.of("Guitar", "Spanish")); // score 4
    private SkillSwapUser weakMatch = new SkillSwapUser(3, "Weak", "uwu", Set.of("Piano"), Set.of("Guitar")); // score 2

    // Creating a FAKE reader instead of StubUsersReader because we need to read the
    // ones created at the top
    // and not the ones held in StubUsersReader
    private FindMatches serviceWith(SkillSwapUser... users) {
        SkillSwapUsersReader fakeReader = new SkillSwapUsersReader() {
            @Override
            public Optional<SkillSwapUser> findById(long id) {
                return List.of(users).stream()
                        .filter(u -> u.id() == id)
                        .findFirst();
            }

            @Override
            public List<SkillSwapUser> findCandidatesFor(long meId) {
                return List.of(users); // strongMatch and weakMatch both have overlap with me, so just return both
                // Also, me is deliberately included for one of the tests below
            }
        };
        return new FindMatches(fakeReader);
    }

    @Test
    void higherScoreSortsFirstTest() {
        FindMatches service = serviceWith(me, weakMatch, strongMatch);

        List<Match> result = service.forUser(1L);

        Assertions.assertThat(result).extracting(Match::matchPersonName)
                .containsExactly("Strong", "Weak");
    }

    @Test
    void requestingUserNeverAppearsInOwnMatchesTest() {
        FindMatches service = serviceWith(me, strongMatch, weakMatch);

        List<Match> result = service.forUser(1L);

        Assertions.assertThat(result).extracting(Match::matchPersonName)
                .doesNotContain("Me");
    }

    @Test
    void unknownUserThrowsUserNotFoundTest() {
        FindMatches service = serviceWith(); // No users passed

        Assertions.assertThatThrownBy(() -> service.forUser(1L)) // No one passed in, so we reader.findById() in .forUser() throws
                .isInstanceOf(UserNotFoundException.class);
    }
}
