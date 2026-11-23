package ie.schrodingerscode.skillswap.block.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.block.application.BlockService;

/**
 * {@code POST} and {@code DELETE /api/blocks/{userId}}: block and unblock a user.
 *
 * <p>No rules in here: it gets the logged-in user from {@link CurrentUser} and calls {@link BlockService}.
 * Both return 204 with no body, the frontend only needs to know it worked.
 */
@RestController
@RequestMapping("/api/blocks")
public class BlockController {

    private final BlockService service;
    private final CurrentUser currentUser;

    public BlockController(BlockService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    /**
     * Blocks a user and removes any request or connection with them.
     *
     * @param userId the user to block
     * @throws ie.schrodingerscode.skillswap.block.domain.exception.SelfBlockException if it is your own ID (400)
     * @throws ie.schrodingerscode.skillswap.block.domain.exception.BlockedUserNotFoundException if the user doesn't exist (404)
     * @throws ie.schrodingerscode.skillswap.block.domain.exception.AlreadyBlockedException if already blocked (409)
     */
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(@PathVariable("userId") long userId) {
        service.block(currentUser.getId(), userId);
    }

    /**
     * Removes a block you placed.
     *
     * @param userId the user to unblock
     * @throws ie.schrodingerscode.skillswap.block.domain.exception.BlockNotFoundException if you haven't blocked them (404)
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable("userId") long userId) {
        service.unblock(currentUser.getId(), userId);
    }
}
