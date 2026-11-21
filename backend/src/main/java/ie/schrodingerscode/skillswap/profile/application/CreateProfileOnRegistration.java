package ie.schrodingerscode.skillswap.profile.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ie.schrodingerscode.skillswap.auth.domain.UserRegistered;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;

/**
 * Gives every new user an empty profile as soon as they register, so the rest of the app never
 * finds a user without one. Listens to auth's UserRegistered event, so auth doesn't need to know about profiles.
 */
@Component
public class CreateProfileOnRegistration {

    private final ProfileRepository profiles;

    public CreateProfileOnRegistration(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    /**
     * Creates the profile with the name from sign-up, no bio and no skills.
     * It runs inside RegisterUser's transaction, so if this fails the user isn't saved either.
     *
     * @param event published by RegisterUser after the user is saved
     */
    @EventListener
    public void on(UserRegistered event) {
        profiles.save(new Profile(event.userId(), event.displayName(), null));
    }
}
