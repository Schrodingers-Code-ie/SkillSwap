package ie.schrodingerscode.skillswap.skill.web;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ie.schrodingerscode.skillswap.common.exception.GlobalExceptionHandler;
import ie.schrodingerscode.skillswap.skill.FakeSkillRepository;
import ie.schrodingerscode.skillswap.skill.application.ListSkillsService;

class SkillControllerTests {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(new SkillController(new ListSkillsService(new FakeSkillRepository())))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listReturnsIdAndNameOfEverySkill() throws Exception {
        mvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Guitar"));
    }

    @Test
    void catalogueCannotBeChangedThroughTheApi() throws Exception {
        // the list is read-only, so there's no POST
        mvc.perform(post("/api/skills").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Chess\"}"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }
}