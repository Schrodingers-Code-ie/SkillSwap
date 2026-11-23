package ie.schrodingerscode.skillswap.block.application;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.block.domain.BlockRepository;

/**
 * The real {@link BlockChecker}, it asks the repository.
 *
 * <p>Kept separate from {@link BlockService} on purpose: BlockService needs connection (to delete the
 * request), and connection needs BlockChecker. If BlockService answered BlockChecker too, each would
 * need the other first and Spring wouldn't start.
 */
@Service
public class BlockLookup implements BlockChecker {

    private final BlockRepository repository;

    public BlockLookup(BlockRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isBlockedEitherWay(long userA, long userB) {
        return repository.existsEitherWay(userA, userB);
    }
}
