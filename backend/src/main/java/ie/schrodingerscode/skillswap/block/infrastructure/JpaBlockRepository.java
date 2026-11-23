package ie.schrodingerscode.skillswap.block.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.block.domain.Block;
import ie.schrodingerscode.skillswap.block.domain.BlockRepository;

/**
 * The real {@link BlockRepository}, it stores blocks in Postgres (the {@code blocks} table).
 *
 * <p>Turns {@link Block} into {@link BlockEntity} and back, so the domain and service don't
 * know about JPA.
 */
@Repository
public class JpaBlockRepository implements BlockRepository {

    private final BlockJpaRepository jpa;

    public JpaBlockRepository(BlockJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Block save(Block block) {
        return jpa.save(BlockEntity.from(block)).toDomain();
    }

    @Override
    public Optional<Block> find(long blockerId, long blockedId) {
        return jpa.findByBlockerIdAndBlockedId(blockerId, blockedId).map(BlockEntity::toDomain);
    }

    @Override
    public boolean existsEitherWay(long userA, long userB) {
        return jpa.existsEitherWay(userA, userB);
    }

    @Override
    public void delete(Block block) {
        jpa.deleteById(block.getId());
    }
}
