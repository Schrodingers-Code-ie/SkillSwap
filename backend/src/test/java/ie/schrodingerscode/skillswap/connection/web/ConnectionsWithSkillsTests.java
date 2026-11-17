package ie.schrodingerscode.skillswap.connection.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.auth.application.RegisterUser;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

//whole app with real postgres, checks the new fields in /api/connections
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional // Each test is rolled back, so tests don't affect each other
class ConnectionsWithSkillsTests {

    private static final String PASSWORD = "secret123";

    //skill ids from the V2 migration
    private static final Map<String, Long> SKILL_ID = Map.of(
            "Guitar", 1L,
            "Spanish", 6L,
            "Baking", 16L,
            "Yoga", 21L);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RegisterUser registerUser;
    @Autowired
    private EntityManager entityManager;

    private MockHttpSession ann;
    private long bobId;
    private long carlId;

    @BeforeEach
    void annIsFriendsWithBobAndCarl() throws Exception {
        ann = signUp("ann@mail.com", "annlogin", "Ann");
        MockHttpSession bob = signUp("bob@mail.com", "boblogin", "Bob");
        MockHttpSession carl = signUp("carl@mail.com", "carllogin", "Carl");

        //ann and bob can swap guitar and spanish
        addSkill(ann, "learn", "Guitar");
        addSkill(ann, "teach", "Spanish");
        addSkill(bob, "teach", "Guitar");
        addSkill(bob, "learn", "Spanish");

        //carl has nothing in common with ann
        addSkill(carl, "teach", "Baking");
        addSkill(carl, "learn", "Yoga");

        bobId = connect(ann, bob);
        carlId = connect(ann, carl);
        entityManager.flush(); // so the match reader sql can see it
    }

    @Test
    void friendWithSharedSkillsHasUsernameAndBothLists() throws Exception {
        mockMvc.perform(get("/api/connections").session(ann))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.userId == " + bobId + ")].displayName").value("Bob"))
                .andExpect(jsonPath("$[?(@.userId == " + bobId + ")].username").value("boblogin"))
                .andExpect(jsonPath("$[?(@.userId == " + bobId + ")].skillsTheyCanTeachMe[0]").value("Guitar"))
                .andExpect(jsonPath("$[?(@.userId == " + bobId + ")].skillsICanTeachThem[0]").value("Spanish"));
    }

    @Test
    void friendWithNoSharedSkillsStillShowsWithEmptyLists() throws Exception {
        mockMvc.perform(get("/api/connections").session(ann))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.userId == " + carlId + ")].username").value("carllogin"))
                .andExpect(jsonPath("$[?(@.userId == " + carlId + ")].skillsTheyCanTeachMe[*]").isEmpty())
                .andExpect(jsonPath("$[?(@.userId == " + carlId + ")].skillsICanTeachThem[*]").isEmpty());
    }

    //register + login, returns the session
    private MockHttpSession signUp(String email, String username, String name) throws Exception {
        registerUser.register(email, username, PASSWORD, name, true);
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }

    //side is learn or teach
    private void addSkill(MockHttpSession session, String side, String skill) throws Exception {
        String proficiency = side.equals("teach") ? ",\"proficiency\":\"BEGINNER\"" : "";
        mockMvc.perform(post("/api/me/" + side + "-skills").with(csrf()).session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\":" + SKILL_ID.get(skill) + proficiency + "}"))
                .andExpect(status().isCreated());
    }

    //send + accept, returns the receiver id
    private long connect(MockHttpSession sender, MockHttpSession receiver) throws Exception {
        long receiverId = myId(receiver);
        String sent = mockMvc.perform(post("/api/requests").with(csrf()).session(sender)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"receiverId\":" + receiverId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long requestId = ((Number) JsonPath.read(sent, "$.id")).longValue();

        mockMvc.perform(post("/api/requests/" + requestId + "/accept").with(csrf()).session(receiver))
                .andExpect(status().isOk());
        return receiverId;
    }

    private long myId(MockHttpSession session) throws Exception {
        String me = mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(me, "$.id")).longValue();
    }
}
