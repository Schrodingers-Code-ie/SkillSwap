package ie.schrodingerscode.skillswap.match.web.dto;

import java.util.List;

import ie.schrodingerscode.skillswap.match.domain.Match;

/**
 * DTO that defines the JSON layout returned by the match endpoint, built from a Match. If the JSON
 * output ever needs to change, change this record instead of MatchAlgorithmController.
 *
 * @param matchPersonId        the other user's ID
 * @param matchPersonName      the other user's display name
 * @param matchPersonUsername  the other user's username
 * @param matchingScore        total number of overlapping skills in both directions
 * @param skillsTheyCanTeachMe skills the other user can teach the current user, sorted alphabetically
 * @param skillsICanTeachThem  skills the current user can teach the other user, sorted alphabetically
 */
public record MatchResponse(
        long matchPersonId,
        String matchPersonName,
        String matchPersonUsername,
        int matchingScore,
        List<String> skillsTheyCanTeachMe,
        List<String> skillsICanTeachThem) {

    /**
     * Converts a Match into a MatchResponse. The skill sets are turned into sorted lists.
     *
     * @param match the match to convert
     * @return      the corresponding response DTO
     */
    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.matchPersonId(),
                match.matchPersonName(),
                match.matchPersonUsername(),
                match.matchingScore(),
                match.skillsTheyCanTeachMe().stream().sorted().toList(), // Sets become sorted lists for stable JSON output
                match.skillsICanTeachThem().stream().sorted().toList());
    }
}
