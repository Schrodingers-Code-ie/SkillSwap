package ie.schrodingerscode.skillswap.block.infrastructure;

import java.time.Instant;

import ie.schrodingerscode.skillswap.block.domain.Block;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * One row of the blocks table.
 * Kept separate from Block so the domain has no JPA annotations (STRUCTURE.md rule 2).
 */
@Entity
@Table(name = "blocks")
public class BlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Postgres hands out the ids
    private Long id;

    @Column(name = "blocker_id", nullable = false)
    private long blockerId;

    @Column(name = "blocked_id", nullable = false)
    private long blockedId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BlockEntity() {
        //JPA needs an empty constructor
    }

    static BlockEntity from(Block block) {
        BlockEntity entity = new BlockEntity();
        entity.id = block.getId(); //null for a new block, so JPA inserts instead of updating
        entity.blockerId = block.getBlockerId();
        entity.blockedId = block.getBlockedId();
        entity.createdAt = block.getCreatedAt();
        return entity;
    }

    Block toDomain() {
        return Block.restore(id, blockerId, blockedId, createdAt);
    }
}
