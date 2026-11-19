package ie.schrodingerscode.skillswap.block.domain;

import java.time.Instant;

import ie.schrodingerscode.skillswap.block.domain.exception.SelfBlockException;

/**
 * One user blocking another. Direction matters: blockerId is the person who clicked "Block",
 * and only they can remove it again.
 *
 * The id is null until the block is saved, because Postgres gives out the ids.
 */
public class Block {

    private final Long id;
    private final long blockerId;
    private final long blockedId;
    private final Instant createdAt;

    private Block(Long id, long blockerId, long blockedId, Instant createdAt) {
        this.id = id;
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.createdAt = createdAt;
    }

    //the only way to make a new block, so blocking yourself can never happen
    public static Block create(long blockerId, long blockedId) {
        if (blockerId == blockedId) {
            throw new SelfBlockException();
        }
        return new Block(null, blockerId, blockedId, Instant.now());
    }

    //rebuilds a block that was already saved, used by the repositories when loading
    public static Block restore(long id, long blockerId, long blockedId, Instant createdAt) {
        return new Block(id, blockerId, blockedId, createdAt);
    }

    public Long getId() {
        return id;
    }

    public long getBlockerId() {
        return blockerId;
    }

    public long getBlockedId() {
        return blockedId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
