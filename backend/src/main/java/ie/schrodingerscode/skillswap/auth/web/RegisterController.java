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

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final RegisterUser registerUser;

    public RegisterController(RegisterUser registerUser) {
        this.registerUser = registerUser;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUser.register(
                request.email(),
                request.password(),
                request.displayName(),
                request.confirmedAdult());
        return UserResponse.from(user);
    }
}
