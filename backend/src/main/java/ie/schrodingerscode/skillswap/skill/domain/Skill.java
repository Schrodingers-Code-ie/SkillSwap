package ie.schrodingerscode.skillswap.skill.domain;

/**
 * One skill in the catalogue. Profiles only store the id, the name comes from here.
 *
 * @param id the skill's id, the same as in the skills table
 * @param name the name shown to users, such as "Guitar"
 */
public record Skill(long id, String name) {

}
