package ie.schrodingerscode.skillswap.match.application;

import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.match.domain.Match;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

public class FindMatchesTests {
    private SkillSwapUser me = new SkillSwapUser(1, "Me", Set.of("Guitar", "Spanish"), Set.of("Piano", "Japanese"));

    private SkillSwapUser strongMatch = new SkillSwapUser(2, "Strong", Set.of("Piano", "Japanese"),
            Set.of("Guitar", "Spanish")); // score 4
    private SkillSwapUser weakMatch = new SkillSwapUser(3, "Weak", Set.of("Piano"), Set.of("Guitar")); // score 2

    // Creating a FAKE reader instead of StubUsersReader because we need to read the
    // ones created at the top
    // and not the ones held in StubUsersReader
    private FindMatches serviceWith(SkillSwapUser... users) {
        SkillSwapUsersReader fakeReader = new SkillSwapUsersReader() {
            @Override
            public List<SkillSwapUser> findAll() {
                return List.of(users);
            }
        };
        return new FindMatches(fakeReader); // Passing this reader with new users to the FindMatches constructor
    }

    @Test
    void higherScoreSortsFirstTest() {
        FindMatches service = serviceWith(me, weakMatch, strongMatch);

        List<Match> result = service.forUser(1L);

        Assertions.assertThat(result).extracting(Match::matchUserName)
                .containsExactly("Strong", "Weak");
    }

    @Test
    void requestingUserNeverAppearsInOwnMatchesTest() {
        FindMatches service = serviceWith(me, strongMatch, weakMatch);

        List<Match> result = service.forUser(1L);

        Assertions.assertThat(result).extracting(Match::matchUserName)
                .doesNotContain("Me");
    }
}
