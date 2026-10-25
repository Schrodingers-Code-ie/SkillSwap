package ie.schrodingerscode.skillswap.profile.web;

import ie.schrodingerscode.skillswap.common.exception.GlobalExceptionHandler;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;
import ie.schrodingerscode.skillswap.skill.FakeSkillRepository;
import ie.schrodingerscode.skillswap.profile.application.GetProfileService;
import ie.schrodingerscode.skillswap.profile.application.ProfileSkillService;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.FakeProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTests {

    private static final long USER_ID = 100L;

    private MockMvc mvc;
    private final ThreadLocal<Boolean> loggedIn = ThreadLocal.withInitial(() -> true);

    @BeforeEach
    void setUp() {
        FakeProfileRepository profiles = new FakeProfileRepository();
        profiles.save(new Profile(USER_ID, "Test User", "Test bio"));
        FakeSkillRepository skills = new FakeSkillRepository();

        loggedIn.set(true);
        ProfileSkillService skillService = new ProfileSkillService(profiles, skills, () -> {
            if (!loggedIn.get()) {
                throw new UnauthorizedException("Not logged in");
            }
            return USER_ID;
        });

        mvc = MockMvcBuilders
                .standaloneSetup(new ProfileController(new GetProfileService(profiles, skills),
                        skillService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static final String TEACH = "/api/me/teach-skills";
    private static final String LEARN = "/api/me/learn-skills";

    @Test
    void addTeachSkillReturns201() throws Exception {
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 11, \"proficiency\": \"BEGINNER\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void addLearnSkillReturns201WithoutProficiency() throws Exception {
        mvc.perform(post(LEARN).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 5}"))
                .andExpect(status().isCreated());
    }

    @Test
    void teachSkillWithoutProficiencyReturns400() throws Exception {
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 12}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void invalidProficiencyReturns400() throws Exception {
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 12, \"proficiency\": \"SUPERHUMAN\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingSkillIdReturns400() throws Exception {
        mvc.perform(post(LEARN).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownSkillReturns404() throws Exception {
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 999, \"proficiency\": \"BEGINNER\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void sameTeachSkillTwiceReturns409() throws Exception {
        String body = "{\"skillId\": 11, \"proficiency\": \"BEGINNER\"}";
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void sameLearnSkillTwiceReturns409() throws Exception {
        mvc.perform(post(LEARN).contentType(MediaType.APPLICATION_JSON).content("{\"skillId\": 5}"))
                .andExpect(status().isCreated());

        mvc.perform(post(LEARN).contentType(MediaType.APPLICATION_JSON).content("{\"skillId\": 5}"))
                .andExpect(status().isConflict());
    }

    @Test
    void removeSkillReturns204ThenSecondRemoveReturns404() throws Exception {
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 11, \"proficiency\": \"BEGINNER\"}"));

        mvc.perform(delete(TEACH + "/11")).andExpect(status().isNoContent());
        mvc.perform(delete(TEACH + "/11")).andExpect(status().isNotFound());
    }

    @Test
    void missingUserReturns401() throws Exception {
        loggedIn.set(false);

        mvc.perform(post(LEARN).contentType(MediaType.APPLICATION_JSON).content("{\"skillId\": 5}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void profileShowsBothLists() throws Exception {
        mvc.perform(post(TEACH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 11, \"proficiency\": \"ADVANCED\"}"));
        mvc.perform(post(LEARN).contentType(MediaType.APPLICATION_JSON)
                .content("{\"skillId\": 5}"));

        mvc.perform(get("/api/users/" + USER_ID + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.teachSkills", hasSize(1)))
                .andExpect(jsonPath("$.teachSkills[0].proficiency").value("ADVANCED"))
                .andExpect(jsonPath("$.learnSkills", hasSize(1)));
    }

    @Test
    void unknownProfileReturns404() throws Exception {
        mvc.perform(get("/api/users/555/profile"))
                .andExpect(status().isNotFound());
    }
}
