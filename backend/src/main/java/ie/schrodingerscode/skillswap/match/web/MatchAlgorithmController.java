package ie.schrodingerscode.skillswap.match.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.match.application.FindMatches;
import ie.schrodingerscode.skillswap.match.web.dto.MatchResponse;

/**
 * REST controller for retrieving matches for the current user.
 * Translates the HTTP request into a call to the FindMatches application service
 * and converts the resulting domain objects  Match) into MatchResponse DTOs.
 */
@RestController
@RequestMapping("/api")
public class MatchAlgorithmController {
    private final FindMatches findMatches;
    private final CurrentUser currentUser;

    /**
     * Creates the controller with the services it needs.
     *
     * @param findMatches service that finds the matches
     * @param currentUser provides the ID of the currently logged-in user
     */
    public MatchAlgorithmController(FindMatches findMatches, CurrentUser currentUser) {
        this.findMatches = findMatches;
        this.currentUser = currentUser;
    }

    /**
     * Handles GET /api/matches. Retrieves all matches for the current user, best match first,
     * and converts each one into a response DTO.
     *
     * @return a list of MatchResponse, one per match, sorted by matching score in descending order
     */
    @GetMapping("/matches")
    public List<MatchResponse> get() {
        return findMatches.forUser(currentUser.getId()).stream()
                .map(MatchResponse::from) // Converts each Match into a MatchResponse
                .toList(); // Collects the converted entries
    }
}
