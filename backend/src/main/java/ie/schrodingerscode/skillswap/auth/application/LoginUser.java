package ie.schrodingerscode.skillswap.auth.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

@Service
public class LoginUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String WRONG_LOGIN = "Wrong email or password";

    public LoginUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String rawEmail, String rawPassword) {
        Email email;
        try {
            email = new Email(rawEmail);
        } catch (InvalidEmailException ex) {
            throw new UnauthorizedException(WRONG_LOGIN);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException(WRONG_LOGIN));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new UnauthorizedException(WRONG_LOGIN);
        }
        return user;
    }
}
