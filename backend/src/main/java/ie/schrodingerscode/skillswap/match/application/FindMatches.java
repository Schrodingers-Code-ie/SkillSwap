package ie.schrodingerscode.skillswap.match.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.match.domain.MatchScorer;
import ie.schrodingerscode.skillswap.match.application.exception.UserNotFoundException;
import ie.schrodingerscode.skillswap.match.domain.Match;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

@Service
public class FindMatches {
    private final SkillSwapUsersReader reader;
    private final MatchScorer scorer = new MatchScorer();

    public FindMatches(SkillSwapUsersReader reader) {
        this.reader = reader;
    }

    public List<Match> forUser(long meId) {
        // Find the user that requested suggestions
        SkillSwapUser me = reader.findById(meId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        List<SkillSwapUser> candidates = reader.findCandidatesFor(meId); // Read in the users who could be a match (everyone except me)

        List<Match> matchingUsers = new ArrayList<>(); // What we will return eventually

        // Loop over the others skipping "me"
        for (SkillSwapUser other : candidates) {
            if (other.id() != meId) {
                Match suggestion = scorer.score(me, other); // Call scorer.score(me, other) for each
                if (suggestion != null)
                    matchingUsers.add(suggestion); // Keep only the ones that returned a result
            }
        }

        // Sort by score, highest first
        matchingUsers.sort(Comparator.comparingInt(Match::matchingScore).reversed());

        // Return the list
        return matchingUsers;
    }
}
