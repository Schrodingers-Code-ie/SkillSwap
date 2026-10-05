package ie.schrodingerscode.skillswap.auth;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Returns who the backend thinks you are. Handy for checking CurrentUser works.
 */
@RestController
@RequestMapping("/api/auth")
public class MeController {

    private final CurrentUser currentUser;

    public MeController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        return Map.of("id", currentUser.getId());
    }
}
