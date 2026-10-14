package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.skill.domain.SkillNotFoundException;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import ie.schrodingerscode.skillswap.profile.domain.Proficiency;

import org.springframework.stereotype.Service;

@Service
public class ProfileSkillService {

    private final ProfileRepository profiles;
    private final SkillRepository skills;
    private final CurrentUser currentUser;

    public ProfileSkillService(ProfileRepository profiles, SkillRepository skills, CurrentUser currentUser) {
        this.profiles = profiles;
        this.skills = skills;
        this.currentUser = currentUser;
    }

    public void addLearnSkill(long skillId) {
        requireSkillExists(skillId);
        Profile profile = loadCurrentProfile();
        profile.addLearnSkill(skillId);
        profiles.save(profile);
    }

    public void addTeachSkill(long skillId, Proficiency proficiency) {
        requireSkillExists(skillId);
        Profile profile = loadCurrentProfile();
        profile.addTeachSkill(skillId, proficiency);
        profiles.save(profile);
    }

    public void removeTeachSkill(long skillId) {
        Profile profile = loadCurrentProfile();
        profile.removeTeachSkill(skillId);
        profiles.save(profile);
    }

    public void removeLearnSkill(long skillId) {
        Profile profile = loadCurrentProfile();
        profile.removeLearnSkill(skillId);
        profiles.save(profile);
    }

    private void requireSkillExists(long skillId) {
        skills.findById(skillId).orElseThrow(() -> new SkillNotFoundException(skillId));
    }

    private Profile loadCurrentProfile() {
        long userId = currentUser.getId();
        return profiles.findById(userId).orElseThrow(() -> new ProfileNotFoundException(userId));
    }
}
