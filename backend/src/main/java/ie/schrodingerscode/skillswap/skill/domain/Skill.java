package ie.schrodingerscode.skillswap.skill.domain;

/**
 * One skill in the catalogue. Profiles only store the id, the name comes from here.
 */
public record Skill(long id, String name) {

}
