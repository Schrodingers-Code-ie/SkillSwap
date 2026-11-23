package ie.schrodingerscode.skillswap.block.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ie.schrodingerscode.skillswap.auth.application.UserChecker;
import ie.schrodingerscode.skillswap.block.domain.Block;
import ie.schrodingerscode.skillswap.block.domain.BlockRepository;
import ie.schrodingerscode.skillswap.block.domain.exception.AlreadyBlockedException;
import ie.schrodingerscode.skillswap.block.domain.exception.BlockNotFoundException;
import ie.schrodingerscode.skillswap.block.domain.exception.BlockedUserNotFoundException;
import ie.schrodingerscode.skillswap.connection.application.ConnectionRemover;

/**
 * Block and unblock use cases, called by {@code BlockController}.
 *
 * <p>Uses {@link UserChecker} from auth to check the other user exists, and {@link ConnectionRemover}
 * from connection to delete any request between the two when one blocks the other.
 */
@Service
public class BlockService {

    private final BlockRepository repository;
    private final UserChecker users;
    private final ConnectionRemover connections;

    public BlockService(BlockRepository repository, UserChecker users, ConnectionRemover connections) {
        this.repository = repository;
        this.users = users;
        this.connections = connections;
    }

    /**
     * Blocks a user and removes any pending request or connection between the two.
     *
     * <p>Runs in one transaction, so if saving the block fails the deleted request comes back too.
     *
     * @param blockerId the logged-in user who clicked "Block"
     * @param blockedId the user being blocked
     * @return the saved block with its ID
     * @throws ie.schrodingerscode.skillswap.block.domain.exception.SelfBlockException if both IDs are the same (400)
     * @throws BlockedUserNotFoundException if there is no user with {@code blockedId} (404)
     * @throws AlreadyBlockedException if this user is already blocked (409)
     */
    @Transactional
    public Block block(long blockerId, long blockedId) {
        Block block = Block.create(blockerId, blockedId); //refuses blocking yourself first
        //otherwise the foreign key in the database rejects it and the API gives a 500
        if (!users.exists(blockedId)) {
            throw new BlockedUserNotFoundException();
        }
        if (repository.find(blockerId, blockedId).isPresent()) {
            throw new AlreadyBlockedException();
        }
        //any pending request or connection between them goes, whoever sent it
        connections.removeBetween(blockerId, blockedId);
        return repository.save(block);
    }

    /**
     * Removes a block the logged-in user placed.
     *
     * <p>It only finds a block placed by {@code blockerId}, so a blocked user can't remove the block on
     * themselves. Unblocking does not bring back the deleted request or connection.
     *
     * @param blockerId the logged-in user who placed the block
     * @param blockedId the user to unblock
     * @throws BlockNotFoundException if {@code blockerId} has not blocked {@code blockedId} (404)
     */
    public void unblock(long blockerId, long blockedId) {
        Block block = repository.find(blockerId, blockedId).orElseThrow(BlockNotFoundException::new);
        repository.delete(block);
    }
}
