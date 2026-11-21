package ie.schrodingerscode.skillswap.profile.application;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;
import ie.schrodingerscode.skillswap.profile.domain.exception.InvalidProfileDetailsException;

/**
 * Changes the name and bio on a profile. The rules (name required, trimmed, length limits)
 * are in Profile, this only loads and saves.
 */
@Service
public class UpdateProfileService {

    private final ProfileRepository profiles;

    public UpdateProfileService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    /**
     * Saves a new name and bio.
     *
     * @param userId the logged in user
     * @param displayName the new name, gets trimmed
     * @param bio the new bio, null or blank removes it
     * @throws ProfileNotFoundException if the user has no profile
     * @throws InvalidProfileDetailsException if the name is blank or too long, or the bio is too long
     */
    public void updateDetails(long userId, String displayName, String bio) {
        Profile profile = profiles.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        profile.updateDetails(displayName, bio);
        profiles.save(profile);
    }
}
