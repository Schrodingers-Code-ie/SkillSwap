package ie.schrodingerscode.skillswap.connection.web.dto;

import java.util.List;

import ie.schrodingerscode.skillswap.match.application.SkillOverlap;

/**
 * One friend, names are null if there's no profile.
 *
 * @param userId the friend's id
 * @param displayName their display name
 * @param username their username
 * @param skillsTheyCanTeachMe skills they teach that I want to learn
 * @param skillsICanTeachThem skills I teach that they want to learn
 */
public record ConnectionResponse(long userId, String displayName, String username,
        List<String> skillsTheyCanTeachMe, List<String> skillsICanTeachThem) {

    public static ConnectionResponse from(SkillOverlap friend, String displayName) {
        return new ConnectionResponse(friend.userId(), displayName, friend.username(),
                friend.skillsTheyCanTeachMe(), friend.skillsICanTeachThem());
    }
}
