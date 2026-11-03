package ie.schrodingerscode.skillswap.profile.application;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;

@Service
public class DisplayNameService implements DisplayNames {

    private final ProfileRepository profiles;

    public DisplayNameService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Override
    public Map<Long, String> displayNamesOf(Collection<Long> userIds) {
        return profiles.displayNamesOf(userIds);
    }
}
