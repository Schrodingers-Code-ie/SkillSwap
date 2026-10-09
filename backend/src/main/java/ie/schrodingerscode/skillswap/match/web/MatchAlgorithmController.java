package ie.schrodingerscode.skillswap.match.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.match.application.FindMatches;
import ie.schrodingerscode.skillswap.match.web.dto.MatchResponse;

@RestController
@RequestMapping("/api")
public class MatchAlgorithmController {
    private final FindMatches findMatches;
    private final CurrentUser currentUser;

    public MatchAlgorithmController(FindMatches findMatches, CurrentUser currentUser) {
        this.findMatches = findMatches;
        this.currentUser = currentUser;
    }

    @GetMapping("/matches")
    public List<MatchResponse> get() {
        return findMatches.forUser(currentUser.getId()).stream()
                .map(MatchResponse::from) // Converts each Match into a MatchResponse class
                .toList(); // Collects the converted MatchResponse entries
    }
}
