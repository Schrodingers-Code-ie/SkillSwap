package ie.schrodingerscode.skillswap.block.domain;

import java.util.Optional;

/**
 * Saves, finds and deletes blocks.
 *
 * <p>The interface is in the domain so the service does not depend on JPA. The real version is
 * {@code JpaBlockRepository}, and the unit tests use {@code InMemoryBlockRepository} instead of a database.
 */
public interface BlockRepository {

    /**
     * Saves a block.
     *
     * @param block the block to save, a new one has a null ID
     * @return the saved block, a new one comes back with the ID Postgres gave it
     */
    Block save(Block block);

    /**
     * Finds the block one user placed on another. Only this direction, not the other way round.
     *
     * @param blockerId the user who blocked
     * @param blockedId the user who was blocked
     * @return the block, or empty if {@code blockerId} has not blocked {@code blockedId}
     */
    Optional<Block> find(long blockerId, long blockedId);

    /**
     * @param userA the ID of one user
     * @param userB the ID of the other user
     * @return true if either user blocked the other
     */
    boolean existsEitherWay(long userA, long userB);

    /**
     * Deletes a saved block.
     *
     * @param block the block to delete, it must have an ID
     */
    void delete(Block block);
}
