package ie.schrodingerscode.skillswap.profile.application;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;

/**
 * The real DisplayNames, it asks the profile repository. Other features only depend on the
 * DisplayNames interface, so they can't reach anything else in profile.
 */
@Service
public class DisplayNameService implements DisplayNames {

    private final ProfileRepository profiles;

    /**
     * Spring passes in the profile repository.
     *
     * @param profiles where the names are read from
     */
    public DisplayNameService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Override
    public Map<Long, String> displayNamesOf(Collection<Long> userIds) {
        return profiles.displayNamesOf(userIds);
    }
}
