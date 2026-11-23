package ie.schrodingerscode.skillswap.profile.domain;

import java.util.Objects;

/**
 * A skill on someone's teach list, together with their level. The level is never null.
 *
 * @param skillId the skill from the catalogue
 * @param proficiency the user's level, never null
 */
public record TeachSkill(long skillId, Proficiency proficiency) {
    /**
     * Refuses a missing level, so every TeachSkill has one.
     *
     * @throws NullPointerException if proficiency is null
     */
    public TeachSkill {
        Objects.requireNonNull(proficiency, "proficiency");
    }
}
