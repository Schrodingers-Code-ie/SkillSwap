package ie.schrodingerscode.skillswap.block.domain;

import java.util.Optional;

/**
 * Where blocks are stored. No Spring or JPA in here on purpose:
 * the Postgres version lives in infrastructure, and the domain doesn't know about it.
 */
public interface BlockRepository {

    //returns the saved block, a new one comes back with the id Postgres gave it
    Block save(Block block);

    //only the block placed by blockerId on blockedId, not the other way round
    Optional<Block> find(long blockerId, long blockedId);

    //true if either user blocked the other
    boolean existsEitherWay(long userA, long userB);

    void delete(Block block);
}
