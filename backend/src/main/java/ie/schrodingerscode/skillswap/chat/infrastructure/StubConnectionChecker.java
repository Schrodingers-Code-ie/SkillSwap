package ie.schrodingerscode.skillswap.chat.infrastructure;

import java.util.Set;

import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.chat.domain.ConnectionChecker;

/**
 * STUB: creates a few fixed connected user pairs for tests.
 * users 1, 2 and 3 are all connected to each other, and everyone else (such as
 * user 4) is not.
 * That matches the ticket's tests: as user 1 message user 2 for 201, and
 * message user 4 for 403.
 * Delete once S4 is merged and use Chris's implementation instead.
 * Remember to also delete chat/domain/ConnectionChecker interface stub.
 */
@Component
public class StubConnectionChecker implements ConnectionChecker {
    private static final Set<Set<Long>> CONNECTED_PAIRS = Set.of(Set.of(1L, 2L), Set.of(1L, 3L), Set.of(2L, 3L));

    @Override
    public boolean areConnected(long userA, long userB) {
        if (userA == userB) {
            return false;
        }
        return CONNECTED_PAIRS.contains(Set.of(userA, userB));
    }
}
