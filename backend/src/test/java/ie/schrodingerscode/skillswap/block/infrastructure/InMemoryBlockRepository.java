package ie.schrodingerscode.skillswap.block.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import ie.schrodingerscode.skillswap.block.domain.Block;
import ie.schrodingerscode.skillswap.block.domain.BlockRepository;

/**
 * Test-only storage in a map, so BlockServiceTests can run as plain JUnit without a database.
 * The app itself uses JpaBlockRepository. This one has no @Repository on purpose,
 * otherwise Spring would find two BlockRepository beans.
 */
public class InMemoryBlockRepository implements BlockRepository {

    private final Map<Long, Block> blocks = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(); //gives 1, 2, 3... like Postgres would

    @Override
    public Block save(Block block) {
        Block saved = block.getId() == null
                ? Block.restore(ids.incrementAndGet(), block.getBlockerId(), block.getBlockedId(), block.getCreatedAt())
                : block;
        blocks.put(saved.getId(), saved);
        return saved;
    }

    @Override
    public Optional<Block> find(long blockerId, long blockedId) {
        return blocks.values().stream()
                .filter(b -> b.getBlockerId() == blockerId && b.getBlockedId() == blockedId)
                .findFirst();
    }

    @Override
    public boolean existsEitherWay(long userA, long userB) {
        return find(userA, userB).isPresent() || find(userB, userA).isPresent();
    }

    @Override
    public void delete(Block block) {
        blocks.remove(block.getId());
    }
}
