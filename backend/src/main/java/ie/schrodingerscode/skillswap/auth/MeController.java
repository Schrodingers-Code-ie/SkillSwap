package ie.schrodingerscode.skillswap.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.UserNotFoundException;
import ie.schrodingerscode.skillswap.auth.web.dto.UserResponse;

/**
 * Returns who the backend thinks you are. Handy for checking CurrentUser works.
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

    @GetMapping("/me")
    public UserResponse me() {
        long id = currentUser.getId();
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
