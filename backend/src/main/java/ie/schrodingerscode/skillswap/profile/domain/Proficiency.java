package ie.schrodingerscode.skillswap.profile.domain;

/**
 * How good someone is at a skill they teach.
 * NONE is never stored: Profile rejects it like a missing level, and the database only allows the other three.
 */

public enum Proficiency {
    NONE, BEGINNER, INTERMEDIATE, ADVANCED
}
