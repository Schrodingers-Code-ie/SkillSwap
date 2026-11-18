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
 * Block and unblock.
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

    //one transaction, so if saving the block fails the deleted request comes back too
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

    //only finds a block I placed, so a blocked user can't remove the block on themselves
    public void unblock(long blockerId, long blockedId) {
        Block block = repository.find(blockerId, blockedId).orElseThrow(BlockNotFoundException::new);
        repository.delete(block);
    }
}
