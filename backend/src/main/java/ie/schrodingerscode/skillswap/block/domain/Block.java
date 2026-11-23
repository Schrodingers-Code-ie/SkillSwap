package ie.schrodingerscode.skillswap.block.domain;

import java.time.Instant;

import ie.schrodingerscode.skillswap.block.domain.exception.SelfBlockException;

/**
 * One user blocking another.
 *
 * <p>Direction matters: {@code blockerId} is the person who clicked "Block", and only they can remove
 * it again. This is the domain version with no JPA annotations, the table row is {@code BlockEntity}.
 * The ID is null until the block is saved, because Postgres gives out the IDs.
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

    /**
     * Creates a new block that is not saved yet. This is the only way to make a new block,
     * so blocking yourself can never happen.
     *
     * @param blockerId the user who blocks
     * @param blockedId the user being blocked
     * @return the new block with a null ID and {@code createdAt} set to now
     * @throws SelfBlockException if both IDs are the same
     */
    public static Block create(long blockerId, long blockedId) {
        if (blockerId == blockedId) {
            throw new SelfBlockException();
        }
        return new Block(null, blockerId, blockedId, Instant.now());
    }

    /**
     * Rebuilds a block that was already saved. Used by the repositories when loading,
     * so it does not check the rules again.
     *
     * @param id the ID from the database
     * @param blockerId the user who blocked
     * @param blockedId the user who was blocked
     * @param createdAt when the block was made
     * @return the block as it was saved
     */
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
