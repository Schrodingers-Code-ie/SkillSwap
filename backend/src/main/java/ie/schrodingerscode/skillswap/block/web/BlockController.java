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
 * Endpoints for blocking and unblocking.
 * No rules in here: it gets the current user and calls the service.
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

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //the frontend only needs to know it worked
    public void block(@PathVariable("userId") long userId) {
        service.block(currentUser.getId(), userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(@PathVariable("userId") long userId) {
        service.unblock(currentUser.getId(), userId);
    }
}
