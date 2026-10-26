package ie.schrodingerscode.skillswap.health;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple endpoint so the frontend (and anyone else) can check the backend is running.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    private final String version;

    public HealthController(@Value("${APP_VERSION:dev}") String version) {
        this.version = version;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "version", version);
    }
}
