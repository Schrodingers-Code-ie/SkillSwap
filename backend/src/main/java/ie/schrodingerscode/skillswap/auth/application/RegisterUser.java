package ie.schrodingerscode.skillswap.auth.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.EmailAlreadyUsedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidPasswordException;

@Service
public class RegisterUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String rawEmail, String rawPassword, String displayName, boolean confirmedAdult) {
        Email email = new Email(rawEmail);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email is already used");
        }
        checkPassword(rawPassword);
        String hashPassword = passwordEncoder.encode(rawPassword);
        User user = User.register(email, hashPassword, displayName, confirmedAdult);
        return userRepository.save(user);
    }

    private void checkPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidPasswordException("Password is required");
        }
        if (rawPassword.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters");
        }
        if (rawPassword.length() > 72) {
            throw new InvalidPasswordException("Password must be at most 72 characters");
        }
    }

}
