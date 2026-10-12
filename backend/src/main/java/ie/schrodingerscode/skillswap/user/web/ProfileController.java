package ie.schrodingerscode.skillswap.user.web;

import ie.schrodingerscode.skillswap.user.application.GetProfileService;
import ie.schrodingerscode.skillswap.user.application.ProfileSkillService;
import ie.schrodingerscode.skillswap.user.application.ProfileView;
import ie.schrodingerscode.skillswap.user.web.dto.AddLearnSkillRequest;
import ie.schrodingerscode.skillswap.user.web.dto.AddTeachSkillRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final GetProfileService getProfile;
    private final ProfileSkillService profileSkills;

    public ProfileController(GetProfileService getProfile, ProfileSkillService profileSkills) {
        this.getProfile = getProfile;
        this.profileSkills = profileSkills;
    }

    @GetMapping("/users/{id}/profile")
    public ProfileView profile(@PathVariable long id) {
        return getProfile.getProfile(id);
    }

    @PostMapping("/me/teach-skills")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTeachSkill(@Valid @RequestBody AddTeachSkillRequest request) {
        profileSkills.addTeachSkill(request.skillId(), request.proficiency());
    }

    @PostMapping("/me/learn-skills")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLearnSkill(@Valid @RequestBody AddLearnSkillRequest request) {
        profileSkills.addLearnSkill(request.skillId());
    }

    @DeleteMapping("/me/teach-skills/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTeachSkill(@PathVariable long skillId) {
        profileSkills.removeTeachSkill(skillId);
    }

    @DeleteMapping("/me/learn-skills/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLearnSkill(@PathVariable long skillId) {
        profileSkills.removeLearnSkill(skillId);
    }
}