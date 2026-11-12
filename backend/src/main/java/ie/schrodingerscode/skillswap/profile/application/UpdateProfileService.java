package ie.schrodingerscode.skillswap.profile.application;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;

@Service
public class UpdateProfileService {

    private final ProfileRepository profiles;

    public UpdateProfileService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    public void updateDetails(long userId, String displayName, String bio) {
        Profile profile = profiles.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        profile.updateDetails(displayName, bio);
        profiles.save(profile);
    }
}
