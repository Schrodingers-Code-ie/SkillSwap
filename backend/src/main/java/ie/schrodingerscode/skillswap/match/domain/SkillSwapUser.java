package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;

/**
 * Holds a user together with the skills they can teach and the skills they want to learn.
 *
 * @param id       ID of the user
 * @param name     display name of the user
 * @param username username of the user
 * @param learn    names of the skills the user wants to learn
 * @param teach    names of the skills the user can teach
 */
public record SkillSwapUser(long id, String name, String username, Set<String> learn, Set<String> teach) {
}
