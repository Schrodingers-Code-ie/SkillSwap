package ie.schrodingerscode.skillswap.auth.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

@Service
public class UserCheckerService implements UserChecker, UserNames {

    private final UserRepository users;

    public UserCheckerService(UserRepository users) {
        this.users = users;
    }

    @Override
    public boolean exists(long userId) {
        return users.findById(userId).isPresent();
    }

    @Override
    public Optional<String> usernameOf(long userId) {
        return users.findById(userId).map(User::getUsername);
    }
}
