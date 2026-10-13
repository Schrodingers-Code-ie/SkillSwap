package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GetProfileService {

    private final ProfileRepository profiles;
    private final SkillRepository skills;

    public GetProfileService(ProfileRepository profiles, SkillRepository skills) {
        this.profiles = profiles;
        this.skills = skills;
    }

    public ProfileView getProfile(long userId) {
        Profile profile = profiles.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        List<ProfileView.TeachSkillView> teach = profile.getTeachSkills().stream()
                .map(t -> new ProfileView.TeachSkillView(t.skillId(), nameOf(t.skillId()), t.proficiency()))
                .toList();

        List<ProfileView.SkillView> learn = profile.getLearnSkillIds().stream()
                .sorted(Comparator.naturalOrder())
                .map(id -> new ProfileView.SkillView(id, nameOf(id)))
                .toList();

        return new ProfileView(profile.getUserId(), profile.getDisplayName(), profile.getBio(), teach, learn);
    }

    private String nameOf(long skillId) {
        return skills.findById(skillId).map(Skill::name).orElse("Unknown skill");
    }
}