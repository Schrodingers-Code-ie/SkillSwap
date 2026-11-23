package ie.schrodingerscode.skillswap.profile.domain;

/**
 * How good someone is at a skill they teach.
 * NONE is never stored: Profile rejects it like a missing level, and the database only allows the other three.
 */
public enum Proficiency {
    /** Never stored. Profile rejects it the same way as a missing level. */
    NONE,
    /** Just starting to teach this skill. */
    BEGINNER,
    /** Comfortable teaching the basics and some more. */
    INTERMEDIATE,
    /** Can teach this skill in depth. */
    ADVANCED
}
