package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;

public record SkillSwapUser(long id, String name, String username, Set<String> learn, Set<String> teach) {
}
