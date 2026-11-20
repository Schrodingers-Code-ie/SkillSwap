package ie.schrodingerscode.skillswap.match.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ie.schrodingerscode.skillswap.match.domain.MatchScorer;
import ie.schrodingerscode.skillswap.match.application.exception.UserNotFoundException;
import ie.schrodingerscode.skillswap.match.domain.Match;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;

/**
 * Application service that returns the users who match with the current user, sorted by matching score
 * in descending order (best match first). Also implements SharedSkills to provide the skill overlap
 * between the current user and other users.
 * Uses SkillSwapUsersReader to load the current user and their candidates, and MatchScorer
 * to score the current user against each candidate. Candidates that don't match are dropped, and the rest
 * are sorted by score.
 */
@Service
public class FindMatches implements SharedSkills {
    private final SkillSwapUsersReader reader;
    private final MatchScorer scorer = new MatchScorer();

    /**
     * Creates the service.
     *
     * @param reader source of users (implemented by a class that reads from Postgres)
     */
    public FindMatches(SkillSwapUsersReader reader) {
        this.reader = reader;
    }

    /**
     * Finds all users who match the current user based on overlapping skills, sorted by matching score
     * in descending order.
     *
     * @param meId                   ID of the current user
     * @return                       matches sorted from highest to lowest score (empty list if there are none)
     * @throws UserNotFoundException if no user exists with the given ID
     */
    public List<Match> forUser(long meId) {
        // Find the user that requested suggestions
        SkillSwapUser me = reader.findById(meId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        // Users with at least one overlapping skill who are not excluded (swap requests, blocks)
        List<SkillSwapUser> candidates = reader.findCandidatesFor(meId);

        List<Match> matchingUsers = new ArrayList<>(); // What we will return eventually

        // Loop over the candidates, skipping "me" as a safety check
        for (SkillSwapUser other : candidates) {
            if (other.id() != meId) {
                Match suggestion = scorer.score(me, other); // Score the pair (me, other)
                if (suggestion != null)
                    matchingUsers.add(suggestion); // Scorer returns null when there is no match, so keep only real matches
            }
        }

        // Sort by score, highest first
        matchingUsers.sort(Comparator.comparingInt(Match::matchingScore).reversed());

        return matchingUsers;
    }

    /**
     * Returns the skill overlap between the current user and each of the given users, in the same order as
     * otherIds. If the current user or one of the other users has no profile, that entry has empty
     * skill lists and a null username if the other user is missing.
     *
     * @param meId     ID of the current user
     * @param otherIds IDs of the other users to compare with
     * @return         one SkillOverlap per ID in otherIds
     */
    @Override
    public List<SkillOverlap> between(long meId, List<Long> otherIds) {
        Optional<SkillSwapUser> me = reader.findById(meId); // only load me once

        return otherIds.stream()
                .map(otherId -> {
                    Optional<SkillSwapUser> other = reader.findById(otherId);
                    if (me.isEmpty() || other.isEmpty()) // no profile, nothing to compare
                        return new SkillOverlap(otherId, other.map(SkillSwapUser::username).orElse(null), List.of(),
                                List.of());

                    return new SkillOverlap(otherId, other.get().username(),
                            List.copyOf(MatchScorer.skillsTheyCanTeachMe(me.get(), other.get())),
                            List.copyOf(MatchScorer.skillsICanTeachThem(me.get(), other.get())));
                })
                .toList();
    }
}
