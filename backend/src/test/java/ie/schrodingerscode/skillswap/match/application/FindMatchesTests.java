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

    @Test
    void betweenGivesTheSameSkillListsAsMatches() {
        FindMatches service = serviceWith(me, strongMatch);

        List<SkillOverlap> result = service.between(1L, List.of(2L));

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).username()).isEqualTo("StronG12");
        Assertions.assertThat(result.get(0).skillsTheyCanTeachMe()).containsExactly("Guitar", "Spanish");
        Assertions.assertThat(result.get(0).skillsICanTeachThem()).containsExactly("Japanese", "Piano");
    }

    @Test
    void betweenKeepsPeopleWithNoOverlapWithEmptyLists() {
        SkillSwapUser noOverlap = new SkillSwapUser(4, "None", "none4", Set.of("Yoga"), Set.of("Baking"));
        FindMatches service = serviceWith(me, noOverlap);

        List<SkillOverlap> result = service.between(1L, List.of(4L));

        Assertions.assertThat(result).hasSize(1); // forUser() would skip them
        Assertions.assertThat(result.get(0).skillsTheyCanTeachMe()).isEmpty();
        Assertions.assertThat(result.get(0).skillsICanTeachThem()).isEmpty();
    }

    @Test
    void betweenKeepsTheOrderOfTheIds() {
        FindMatches service = serviceWith(me, strongMatch, weakMatch);

        Assertions.assertThat(service.between(1L, List.of(3L, 2L)))
                .extracting(SkillOverlap::userId)
                .containsExactly(3L, 2L);
    }

    @Test
    void betweenWithUnknownUserGivesEmptyLists() {
        FindMatches service = serviceWith(me); // User 9 doesn't exist

        List<SkillOverlap> result = service.between(1L, List.of(9L));

        Assertions.assertThat(result.get(0).username()).isNull();
        Assertions.assertThat(result.get(0).skillsTheyCanTeachMe()).isEmpty();
    }
}
