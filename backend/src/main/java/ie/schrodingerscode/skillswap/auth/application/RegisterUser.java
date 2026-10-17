package ie.schrodingerscode.skillswap.auth.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRegistered;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.EmailAlreadyUsedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidDisplayNameException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidPasswordException;
import ie.schrodingerscode.skillswap.auth.domain.exception.UsernameAlreadyUsedException;

@Service
public class RegisterUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

    public RegisterUser(UserRepository userRepository, PasswordEncoder passwordEncoder,
            ApplicationEventPublisher events) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.events = events;
    }

    // One transaction for the user and the profile: the listener runs inside publishEvent,
    // so if saving the profile fails the user insert is rolled back too
    @Transactional
    public User register(String rawEmail, String username, String rawPassword, String displayName,
            boolean confirmedAdult) {
        Email email = new Email(rawEmail);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email is already used");
        }
        checkPassword(rawPassword);
        checkDisplayName(displayName);

        String hashPassword = passwordEncoder.encode(rawPassword);
        User user = User.register(email, username, hashPassword, confirmedAdult);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyUsedException("Username is already taken");
        }
        User saved = userRepository.save(user);
        events.publishEvent(new UserRegistered(saved.getId(), displayName.trim()));
        return saved;
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

    private void checkDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new InvalidDisplayNameException("Display name is required");
        }
        if (displayName.length() > 100) {
            throw new InvalidDisplayNameException("Display name must be at most 100 characters");
        }
    }
}
