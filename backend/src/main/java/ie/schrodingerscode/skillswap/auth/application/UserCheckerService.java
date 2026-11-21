package ie.schrodingerscode.skillswap.auth.application;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

@Service
public class UserCheckerService implements UserChecker {

    private final UserRepository users;

    public UserCheckerService(UserRepository users) {
        this.users = users;
    }

    @Override
    public boolean exists(long userId) {
        return users.findById(userId).isPresent();
    }
}
