package ie.schrodingerscode.skillswap.connection.web.dto;

import java.util.List;

import ie.schrodingerscode.skillswap.match.application.SkillOverlap;

//one friend, names are null if there's no profile
public record ConnectionResponse(long userId, String displayName, String username,
        List<String> skillsTheyCanTeachMe, List<String> skillsICanTeachThem) {

    public static ConnectionResponse from(SkillOverlap friend, String displayName) {
        return new ConnectionResponse(friend.userId(), displayName, friend.username(),
                friend.skillsTheyCanTeachMe(), friend.skillsICanTeachThem());
    }
}
