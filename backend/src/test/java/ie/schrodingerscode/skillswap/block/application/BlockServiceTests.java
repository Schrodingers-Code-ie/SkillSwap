package ie.schrodingerscode.skillswap.block.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.application.UserChecker;
import ie.schrodingerscode.skillswap.block.domain.exception.AlreadyBlockedException;
import ie.schrodingerscode.skillswap.block.domain.exception.BlockNotFoundException;
import ie.schrodingerscode.skillswap.block.domain.exception.SelfBlockException;
import ie.schrodingerscode.skillswap.block.infrastructure.InMemoryBlockRepository;
import ie.schrodingerscode.skillswap.connection.application.SwapRequestService;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.exception.BlockedRequestException;
import ie.schrodingerscode.skillswap.connection.infrastructure.InMemorySwapRequestRepository;

//plain JUnit with in-memory repositories, uses the real SwapRequestService to check both work together
class BlockServiceTests {

    private BlockService blocks;
    private SwapRequestService requests;

    @BeforeEach
    void setUp() {
        UserChecker users = userId -> true; //every user exists
        InMemoryBlockRepository blockRepository = new InMemoryBlockRepository();
        requests = new SwapRequestService(new InMemorySwapRequestRepository(), users, new BlockLookup(blockRepository),
                (meId, otherIds) -> List.of(), event -> {
                });
        blocks = new BlockService(blockRepository, users, requests);
    }

    @Test
    void blockingRemovesTheConnection() {
        SwapRequest request = requests.send(2, 1);
        requests.accept(request.getId(), 1);

        blocks.block(1, 2);

        assertThat(requests.areConnected(1, 2)).isFalse(); //so chat refuses messages too
    }

    @Test
    void neitherSideCanSendARequestAfterABlock() {
        blocks.block(1, 2);

        assertThatThrownBy(() -> requests.send(1, 2)).isInstanceOf(BlockedRequestException.class);
        assertThatThrownBy(() -> requests.send(2, 1)).isInstanceOf(BlockedRequestException.class);
    }

    @Test
    void cannotBlockYourselfOrTheSamePersonTwice() {
        assertThatThrownBy(() -> blocks.block(1, 1)).isInstanceOf(SelfBlockException.class);

        blocks.block(1, 2);
        assertThatThrownBy(() -> blocks.block(1, 2)).isInstanceOf(AlreadyBlockedException.class);
    }

    @Test
    void blockedUserCannotRemoveTheBlock() {
        blocks.block(1, 2);

        assertThatThrownBy(() -> blocks.unblock(2, 1)).isInstanceOf(BlockNotFoundException.class);
    }

    @Test
    void afterUnblockingTheyCanSendANewRequest() {
        blocks.block(1, 2);
        blocks.unblock(1, 2);

        requests.send(2, 1);
    }
}
