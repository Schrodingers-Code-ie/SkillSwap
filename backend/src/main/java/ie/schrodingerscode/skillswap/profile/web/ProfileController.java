package ie.schrodingerscode.skillswap.profile.web;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.profile.application.GetProfileService;
import ie.schrodingerscode.skillswap.profile.application.ProfileSkillService;
import ie.schrodingerscode.skillswap.profile.application.ProfileView;
import ie.schrodingerscode.skillswap.profile.application.UpdateProfileService;
import ie.schrodingerscode.skillswap.profile.web.dto.AddLearnSkillRequest;
import ie.schrodingerscode.skillswap.profile.web.dto.AddTeachSkillRequest;
import ie.schrodingerscode.skillswap.profile.web.dto.UpdateProfileRequest;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;
import ie.schrodingerscode.skillswap.profile.domain.exception.DuplicateSkillException;
import ie.schrodingerscode.skillswap.profile.domain.exception.InvalidProfileDetailsException;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProficiencyRequiredException;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;
import ie.schrodingerscode.skillswap.profile.domain.exception.SkillNotInListException;
import ie.schrodingerscode.skillswap.skill.domain.SkillNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for user story 2. Anyone logged in can read any profile,
 * but changes always go to your own profile (the /me paths).
 */
@RestController
@RequestMapping("/api")
public class ProfileController {

    private final GetProfileService getProfile;
    private final ProfileSkillService profileSkills;
    private final UpdateProfileService updateProfile;
    private final CurrentUser currentUser;

    public ProfileController(GetProfileService getProfile, ProfileSkillService profileSkills,
            UpdateProfileService updateProfile, CurrentUser currentUser) {
        this.getProfile = getProfile;
        this.profileSkills = profileSkills;
        this.updateProfile = updateProfile;
        this.currentUser = currentUser;
    }

    /**
     * GET /api/users/{id}/profile, used by the profile page and the side panels.
     *
     * @param id the user whose profile to show
     * @return name, bio and both skill lists
     * @throws ProfileNotFoundException if there's no profile (404)
     */
    @GetMapping("/users/{id}/profile")
    public ProfileView profile(@PathVariable long id) {
        return getProfile.getProfile(id);
    }

    /**
     * PUT /api/me/profile, changes your name and bio.
     *
     * @param request the new name and an optional bio, checked by the annotations first (400)
     * @return the updated profile, so the page doesn't need to load it again
     * @throws InvalidProfileDetailsException if the name or bio breaks a rule (400)
     * @throws UnauthorizedException if nobody is logged in (401)
     */
    @PutMapping("/me/profile")
    public ProfileView updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        long userId = currentUser.getId();
        updateProfile.updateDetails(userId, request.displayName(), request.bio());
        return getProfile.getProfile(userId);
    }

    /**
     * POST /api/me/teach-skills, adds a skill you can teach. Returns 201 with no body.
     *
     * @param request the skill id and your level
     * @throws SkillNotFoundException if the skill isn't in the catalogue (404)
     * @throws ProficiencyRequiredException if the level is missing (400)
     * @throws DuplicateSkillException if it's already on your teach list (409)
     * @throws UnauthorizedException if nobody is logged in (401)
     */
    @PostMapping("/me/teach-skills")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTeachSkill(@Valid @RequestBody AddTeachSkillRequest request) {
        profileSkills.addTeachSkill(request.skillId(), request.proficiency());
    }

    /**
     * POST /api/me/learn-skills, adds a skill you want to learn. Returns 201 with no body.
     *
     * @param request the skill id
     * @throws SkillNotFoundException if the skill isn't in the catalogue (404)
     * @throws DuplicateSkillException if it's already on your learn list (409)
     * @throws UnauthorizedException if nobody is logged in (401)
     */
    @PostMapping("/me/learn-skills")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLearnSkill(@Valid @RequestBody AddLearnSkillRequest request) {
        profileSkills.addLearnSkill(request.skillId());
    }

    /**
     * DELETE /api/me/teach-skills/{skillId}, removes a skill you can teach. Returns 204.
     *
     * @param skillId the skill to remove
     * @throws SkillNotInListException if it isn't on your teach list (404)
     * @throws UnauthorizedException if nobody is logged in (401)
     */
    @DeleteMapping("/me/teach-skills/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTeachSkill(@PathVariable long skillId) {
        profileSkills.removeTeachSkill(skillId);
    }

    /**
     * DELETE /api/me/learn-skills/{skillId}, removes a skill you want to learn. Returns 204.
     *
     * @param skillId the skill to remove
     * @throws SkillNotInListException if it isn't on your learn list (404)
     * @throws UnauthorizedException if nobody is logged in (401)
     */
    @DeleteMapping("/me/learn-skills/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLearnSkill(@PathVariable long skillId) {
        profileSkills.removeLearnSkill(skillId);
    }
}
