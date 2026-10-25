package ie.schrodingerscode.skillswap.match.infrastructure;

import java.util.Map;
import static java.util.Map.entry;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.auth.application.RegisterUser;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.match.domain.SkillSwapUser;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
class JdbcUsersReaderTests {

    private static final String PASSWORD = "secret123";

    // To avoid looking up skills in V2 all the time, map numbers to skills
    private static final Map<String, Long> SKILL_ID = Map.ofEntries(
            entry("Guitar", 1L),
            entry("Piano", 2L),
            entry("Singing", 3L),
            entry("Drums", 4L),
            entry("Music Production", 5L),
            entry("Spanish", 6L),
            entry("French", 7L),
            entry("Irish", 8L),
            entry("German", 9L),
            entry("Japanese", 10L),
            entry("Java", 11L),
            entry("Python", 12L),
            entry("JavaScript", 13L),
            entry("SQL", 14L),
            entry("Web Design", 15L),
            entry("Baking", 16L),
            entry("Meal Prep", 17L),
            entry("Italian Cuisine", 18L),
            entry("Vegetarian Cooking", 19L),
            entry("Cocktail Making", 20L),
            entry("Yoga", 21L),
            entry("Running", 22L),
            entry("Weight Training", 23L),
            entry("Climbing", 24L),
            entry("Swimming", 25L));

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RegisterUser registerUser;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcUsersReader usersReader;

    private Long ulvenId, janeId, mikeId, jessId, doeId;

    @BeforeEach
    void registerAndAddSkills() throws Exception {
        ulvenId = signUp("ulven@mail.com", "ulvenrev", "Ulven",
                Set.of("Guitar", "Spanish", "French", "Python"),
                Set.of("Piano", "Japanese", "JavaScript", "Java"));
        janeId = signUp("jane@mail.com", "JJJ", "Jane",
                Set.of("JavaScript"), Set.of("Guitar", "Spanish"));
        mikeId = signUp("mike@mail.com", "mi6", "Mike",
                Set.of("Piano", "Japanese"), Set.of("Guitar", "Spanish", "Yoga"));
        jessId = signUp("jess@mail.com", "jess", "Jessica",
                Set.of("Japanese"), Set.of("SQL"));
        doeId = signUp("doe@mail.com", "doejane", "Doe",
                Set.of("Python"), Set.of("Baking", "Piano"));

        entityManager.flush(); // Forces Hibernate to write everything in the database immediately, then we can safely call SQL query
    }

    private Long signUp(String email, String username, String name,
            Set<String> learn, Set<String> teach) throws Exception {
        User user = registerUser.register(email, username, PASSWORD, name, true); // Register a user
        MockHttpSession session = login(email); // Log them in, keeping the session cookie

        for (String skill : learn) {
            mockMvc.perform(post("/api/me/learn-skills").session(session) // Add skills the same way you would in Postman
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"skillId\":" + SKILL_ID.get(skill) + "}"))
                    .andExpect(status().isCreated());
        }
        for (String skill : teach) {
            mockMvc.perform(post("/api/me/teach-skills").session(session)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"skillId\":" + SKILL_ID.get(skill)
                            + ",\"proficiency\":\"BEGINNER\"}"))
                    .andExpect(status().isCreated());
        }
        return user.getId(); // What will be passed in to JdbcUsersReader's methods
    }

    private MockHttpSession login(String email) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login") // Log in the same way you would in Postman
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }

    @Test
    void findByIdReturnsNameAndSkills() {
        SkillSwapUser ulven = usersReader.findById(ulvenId).orElseThrow();

        assertThat(ulven.name()).isEqualTo("Ulven");
        assertThat(ulven.username()).isEqualTo("ulvenrev");
        assertThat(ulven.learn()).containsExactlyInAnyOrder("Guitar", "Spanish", "French", "Python");
        assertThat(ulven.teach()).containsExactlyInAnyOrder("Piano", "Japanese", "JavaScript", "Java");
    }

    @Test
    void findByIdReturnsEmptyForUnknownUser() {
        assertThat(usersReader.findById(-1L)).isEmpty();
    }

    @Test
    void findCandidatesForExcludesMeAndPeopleWithoutOverlap() {
        assertThat(usersReader.findCandidatesFor(ulvenId))
                .extracting(SkillSwapUser::name)
                .containsExactlyInAnyOrder("Jane", "Mike", "Jessica"); // Doe has no overlap
    }

    @Test
    void userInSwapRequestsDoesntShowAsMatchTest() throws Exception {
        // Sending a request from Ulven to Jane
        MockHttpSession session = login("ulven@mail.com");
        mockMvc.perform(post("/api/requests").session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\": " + janeId + "}"))
                .andExpect(status().isCreated());

        assertThat(usersReader.findCandidatesFor(ulvenId))
                .extracting(SkillSwapUser::name)
                .containsExactlyInAnyOrder("Mike", "Jessica"); // Jane doesn't appear in the suggestions anymore
    }
}
