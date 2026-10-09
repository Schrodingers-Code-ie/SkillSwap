package ie.schrodingerscode.skillswap.chat;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.chat.infrastructure.StubConnectionChecker;

class StubConnectionCheckerTest {

    private final StubConnectionChecker checker = new StubConnectionChecker();

    @Test
    void fixedPairsAreConnectedInBothDirections() {
        assertThat(checker.areConnected(1, 2)).isTrue();
        assertThat(checker.areConnected(2, 1)).isTrue();
        assertThat(checker.areConnected(1, 3)).isTrue();
        assertThat(checker.areConnected(2, 3)).isTrue();
        assertThat(checker.areConnected(3, 2)).isTrue();
    }

    @Test
    void otherPairsAreNotConnected() {
        assertThat(checker.areConnected(1, 4)).isFalse();
        assertThat(checker.areConnected(4, 1)).isFalse();
    }

    @Test
    void nobodyIsConnectedToThemselves() {
        assertThat(checker.areConnected(1, 1)).isFalse();
        assertThat(checker.areConnected(2, 2)).isFalse();
        assertThat(checker.areConnected(3, 3)).isFalse();
        assertThat(checker.areConnected(4, 4)).isFalse();
    }
}