package ie.schrodingerscode.skillswap.match.domain;

import java.util.Set;

public record SkillSwapUser(long id, String name, Set<String> learn, Set<String> teach) {
}
