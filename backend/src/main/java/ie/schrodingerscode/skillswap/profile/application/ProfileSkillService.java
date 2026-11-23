package ie.schrodingerscode.skillswap.profile.application;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.skill.domain.SkillNotFoundException;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProfileNotFoundException;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;
import ie.schrodingerscode.skillswap.profile.domain.exception.DuplicateSkillException;
import ie.schrodingerscode.skillswap.profile.domain.exception.ProficiencyRequiredException;
import ie.schrodingerscode.skillswap.profile.domain.exception.SkillNotInListException;

import org.springframework.stereotype.Service;

/**
 * Adds and removes skills on the logged-in user's own teach and learn lists.
 * It always works on the current user, so nobody can change someone else's skills.
 * The skill is checked against the catalogue first, so an unknown id gives a 404 and not a database error.
 */
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

    /**
     * Adds a skill the user wants to learn. No level is needed.
     *
     * @param skillId a skill from the catalogue
     * @throws SkillNotFoundException if no skill has this id
     * @throws DuplicateSkillException if it's already on the learn list
     * @throws ProfileNotFoundException if the user has no profile
     * @throws UnauthorizedException if nobody is logged in
     */
    public void addLearnSkill(long skillId) {
        requireSkillExists(skillId);
        Profile profile = loadCurrentProfile();
        profile.addLearnSkill(skillId);
        profiles.save(profile);
    }

    /**
     * Adds a skill the user can teach, with their level.
     *
     * @param skillId a skill from the catalogue
     * @param proficiency how good they are at it, has to be BEGINNER, INTERMEDIATE or ADVANCED
     * @throws SkillNotFoundException if no skill has this id, checked before the level
     * @throws ProficiencyRequiredException if the level is missing or NONE
     * @throws DuplicateSkillException if it's already on the teach list
     * @throws ProfileNotFoundException if the user has no profile
     * @throws UnauthorizedException if nobody is logged in
     */
    public void addTeachSkill(long skillId, Proficiency proficiency) {
        requireSkillExists(skillId);
        Profile profile = loadCurrentProfile();
        profile.addTeachSkill(skillId, proficiency);
        profiles.save(profile);
    }

    /**
     * Removes a skill from the teach list.
     *
     * @param skillId the skill to remove
     * @throws SkillNotInListException if it isn't on the teach list
     * @throws ProfileNotFoundException if the user has no profile
     * @throws UnauthorizedException if nobody is logged in
     */
    public void removeTeachSkill(long skillId) {
        Profile profile = loadCurrentProfile();
        profile.removeTeachSkill(skillId);
        profiles.save(profile);
    }

    /**
     * Removes a skill from the learn list.
     *
     * @param skillId the skill to remove
     * @throws SkillNotInListException if it isn't on the learn list
     * @throws ProfileNotFoundException if the user has no profile
     * @throws UnauthorizedException if nobody is logged in
     */
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
