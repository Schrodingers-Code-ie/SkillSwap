package ie.schrodingerscode.skillswap.user.domain;

import java.util.Objects;

public record TeachSkill(long skillId, Proficiency proficiency) {
    public TeachSkill {
        Objects.requireNonNull(proficiency, "proficiency");
    }
}