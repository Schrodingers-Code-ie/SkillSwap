package ie.schrodingerscode.skillswap.auth.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

/**
 * Use case: check an email and password and return the matching user.
 *
 * <p>This class only checks the details. Starting the session (the cookie) is done by
 * {@code LoginController}. Every kind of failure gives the same "Wrong email or password" message, so
 * nobody can use the login form to find out which emails have an account.
 */
@Service
public class LoginUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String WRONG_LOGIN = "Wrong email or password";

    public LoginUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Finds the user by email and checks the password against the stored bcrypt hash.
     *
     * @param rawEmail the email as typed, it is trimmed and lower-cased first
     * @param rawPassword the plain password as typed
     * @return the logged-in user
     * @throws UnauthorizedException if the email is badly formatted, has no account, or the password
     *         is wrong (401, same message for all three)
     */
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
