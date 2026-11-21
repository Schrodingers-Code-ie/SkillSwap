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

/**
 * Use case: create a new account from the sign up form.
 *
 * <p>It checks the input, hashes the password with bcrypt, saves the user and then publishes
 * {@link UserRegistered} so the profile feature can create the profile with the display name.
 * It does not log the user in, the frontend calls login right after.
 */
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

    /**
     * Checks the sign up data, saves the user and publishes {@link UserRegistered}.
     *
     * <p>It runs in one transaction for the user and the profile. The profile listener runs inside
     * {@code publishEvent}, so if saving the profile fails, the user insert is rolled back too and we
     * never end up with a user that has no profile.
     *
     * @param rawEmail the email as typed
     * @param username the username as typed
     * @param rawPassword the plain password, 8 to 72 characters
     * @param displayName the full name shown on the profile, at most 100 characters
     * @param confirmedAdult whether the user confirmed they are 18 or older
     * @return the saved user with its new ID
     * @throws ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException if the email is not valid (400)
     * @throws InvalidPasswordException if the password is empty or the wrong length (400)
     * @throws InvalidDisplayNameException if the display name is empty or too long (400)
     * @throws ie.schrodingerscode.skillswap.auth.domain.exception.InvalidUsernameException if the username breaks the rules (400)
     * @throws ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException if {@code confirmedAdult} is false (400)
     * @throws EmailAlreadyUsedException if the email already has an account (409)
     * @throws UsernameAlreadyUsedException if the username is taken, ignoring case (409)
     */
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
