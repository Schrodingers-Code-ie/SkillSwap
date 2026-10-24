package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;

import org.assertj.core.api.Assertions; // Using AssertJ and not JUnit, because AssertJ is more convinient for checking collections
import org.junit.jupiter.api.Test;

public class MatchScorerTests {
    private MatchScorer scorer = new MatchScorer();

    @Test
    void twoWayOverlapReturnsAMatchTest() {
        SkillSwapUser me = new SkillSwapUser(1, "Me", "me123", Set.of("Guitar"), Set.of("Piano"));
        SkillSwapUser other = new SkillSwapUser(2, "Other", "o12", Set.of("Piano"), Set.of("Guitar"));

        Match match = scorer.score(me, other);

        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(match.skillsTheyCanTeachMe()).containsExactly("Guitar");
        Assertions.assertThat(match.skillsICanTeachThem()).containsExactly("Piano");
        Assertions.assertThat(match.matchingScore()).isEqualTo(2);
    }

    @Test
    void oneWayOverlapITeachReturnsAMatchTest() {
        SkillSwapUser me = new SkillSwapUser(1, "Me", "me123", Set.of("Guitar"), Set.of("Piano"));
        SkillSwapUser other = new SkillSwapUser(2, "Other", "o12", Set.of("Piano"), Set.of("Cooking"));

        Match match = scorer.score(me, other);

        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(match.skillsTheyCanTeachMe()).containsExactly();
        Assertions.assertThat(match.skillsICanTeachThem()).containsExactly("Piano");
        Assertions.assertThat(match.matchingScore()).isEqualTo(1);
    }

    @Test
    void oneWayOverlapILearnReturnsAMatchTest() {
        SkillSwapUser me = new SkillSwapUser(1, "Me", "me123", Set.of("Guitar"), Set.of("Piano"));
        SkillSwapUser other = new SkillSwapUser(2, "Other", "o12", Set.of("Cooking"), Set.of("Guitar"));

        Match match = scorer.score(me, other);

        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(match.skillsTheyCanTeachMe()).containsExactly("Guitar");
        Assertions.assertThat(match.skillsICanTeachThem()).containsExactly();
        Assertions.assertThat(match.matchingScore()).isEqualTo(1);
    }

    @Test
    void noOverlapAtAllIsNotAMatchTest() {
        SkillSwapUser me = new SkillSwapUser(1, "Me", "me123", Set.of("Guitar"), Set.of("Piano"));
        SkillSwapUser other = new SkillSwapUser(2, "Other", "o12", Set.of("Yoga"), Set.of("SQL"));

        Assertions.assertThat(scorer.score(me, other)).isNull();
    }

    @Test
    void scoreIsOnePointPerOverlappingSkillInEitherDirectionTest() {
        SkillSwapUser me = new SkillSwapUser(1, "Me", "me123", Set.of("Guitar", "Spanish"),
                Set.of("Piano", "Japanese"));
        SkillSwapUser other = new SkillSwapUser(2, "Other", "o12", Set.of("Piano", "Japanese"),
                Set.of("Guitar", "Spanish"));

        Assertions.assertThat(scorer.score(me, other).matchingScore()).isEqualTo(4);
    }

    @Test
    void threeOverlapsInSkillsInBothDirectionsReturnsMatchingScoreThreeTest() {
        SkillSwapUser me = new SkillSwapUser(1, "Ulven", "ulvenrev", Set.of("Guitar", "Spanish", "French", "Python"),
                Set.of("Piano", "Japanese", "JavaScript", "Java"));
        SkillSwapUser other = new SkillSwapUser(2, "Jane", "JJJ", Set.of("JavaScript"), Set.of("Guitar", "Spanish"));

        Match match = scorer.score(me, other);

        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(match.skillsTheyCanTeachMe()).containsExactly("Guitar", "Spanish");
        Assertions.assertThat(match.skillsICanTeachThem()).containsExactly("JavaScript");
        Assertions.assertThat(match.matchingScore()).isEqualTo(3);
    }

}
