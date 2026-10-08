package ie.schrodingerscode.skillswap.match.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.match.application.FindMatches;
import ie.schrodingerscode.skillswap.match.domain.Match;

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
    public List<Match> get() {
        return findMatches.forUser(currentUser.getId());
    }
}
