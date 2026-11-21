package ie.schrodingerscode.skillswap.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.UserNotFoundException;
import ie.schrodingerscode.skillswap.auth.web.dto.UserResponse;

/**
 * {@code GET /api/auth/me}: returns the logged-in user.
 *
 * <p>The frontend calls it before showing any logged-in page (protectedLoader in router/loaders.jsx). A 401
 * means the session is missing or expired, so the user is sent to the login page.
 */
@RestController
@RequestMapping("/api/auth")
public class MeController {

    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    public MeController(CurrentUser currentUser, UserRepository userRepository) {
        this.currentUser = currentUser;
        this.userRepository = userRepository;
    }

    /**
     * Returns the user from the current session.
     *
     * @return the user's ID, email and username
     * @throws ie.schrodingerscode.skillswap.common.exception.UnauthorizedException if nobody is logged in (401)
     * @throws UserNotFoundException if the session points to a user that no longer exists (404)
     */
    @GetMapping("/me")
    public UserResponse me() {
        long id = currentUser.getId();
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
