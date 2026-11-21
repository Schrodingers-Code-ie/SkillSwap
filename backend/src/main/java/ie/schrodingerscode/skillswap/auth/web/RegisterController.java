package ie.schrodingerscode.skillswap.auth.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.application.RegisterUser;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.web.dto.RegisterRequest;
import ie.schrodingerscode.skillswap.auth.web.dto.UserResponse;
import jakarta.validation.Valid;

/**
 * {@code POST /api/auth/register}: creates a new account.
 *
 * <p>It only passes the JSON body to {@link RegisterUser}. It does not log the user in, the sign up
 * page calls login straight after.
 */
@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final RegisterUser registerUser;

    public RegisterController(RegisterUser registerUser) {
        this.registerUser = registerUser;
    }

    /**
     * Registers a new user and returns 201 Created.
     *
     * @param request the sign up form from the JSON body
     * @return the new user's ID, email and username
     * @throws ie.schrodingerscode.skillswap.common.exception.ValidationException if a field breaks a rule (400)
     * @throws ie.schrodingerscode.skillswap.common.exception.ConflictException if the email or username is taken (409)
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUser.register(
                request.email(),
                request.username(),
                request.password(),
                request.displayName(),
                request.confirmedAdult());
        return UserResponse.from(user);
    }
}
