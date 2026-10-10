package ie.schrodingerscode.skillswap.match.web.dto;

import java.util.List;

import ie.schrodingerscode.skillswap.match.domain.Match;

// A layout for the JSON we see in the return
// If we ever need to change JSON, we change THIS FILE instead of MatchAlgorithmContoller
public record MatchResponse(
        long matchUserID,
        String matchUserName,
        int matchingScore,
        List<String> skillsTheyCanTeachMe,
        List<String> skillsICanTeachThem) {

    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.matchUserID(),
                match.matchUserName(),
                match.matchingScore(),
                match.skillsTheyCanTeachMe().stream().sorted().toList(), // With .stream() we can use .sorted and
                                                                         // .toList
                match.skillsICanTeachThem().stream().sorted().toList());
    }
}
