package ie.schrodingerscode.skillswap.profile.domain;

import java.util.Objects;

/**
 * A skill on someone's teach list, together with their level. The level is never null.
 */

public record TeachSkill(long skillId, Proficiency proficiency) {
    public TeachSkill {
        Objects.requireNonNull(proficiency, "proficiency");
    }
}
