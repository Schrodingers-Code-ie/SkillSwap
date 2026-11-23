package ie.schrodingerscode.skillswap.block.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data repository for {@link BlockEntity}.
 *
 * <p>Spring writes the SQL for {@code findByBlockerIdAndBlockedId} from its name, and
 * {@code existsEitherWay} has its own query because it checks both directions.
 * Only {@link JpaBlockRepository} uses it, the rest of the app goes through {@code BlockRepository}.
 */
public interface BlockJpaRepository extends JpaRepository<BlockEntity, Long> {

    Optional<BlockEntity> findByBlockerIdAndBlockedId(long blockerId, long blockedId);

    //A blocked B, or B blocked A
    @Query("""
            SELECT COUNT(b) > 0 FROM BlockEntity b
            WHERE (b.blockerId = :userA AND b.blockedId = :userB)
               OR (b.blockerId = :userB AND b.blockedId = :userA)
            """)
    boolean existsEitherWay(@Param("userA") long userA, @Param("userB") long userB);
}
