package ie.schrodingerscode.skillswap.profile.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.auth.domain.UserRegistered;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;

@Component
public class CreateProfileOnRegistration {

    private final ProfileRepository profiles;

    public CreateProfileOnRegistration(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    @EventListener
    public void on(UserRegistered event) {
        profiles.save(new Profile(event.userId(), event.displayName(), null));
    }
}