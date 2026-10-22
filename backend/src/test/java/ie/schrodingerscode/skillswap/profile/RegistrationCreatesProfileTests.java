package ie.schrodingerscode.skillswap.profile;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class RegistrationCreatesProfileTests {

    private static final String EMAIL = "ann.profile@mail.com";
    private static final String PASSWORD = "secret123";

    @Autowired
    MockMvc mvc;

    @Test
    void registeredUserHasEmptyProfile() throws Exception {
        String body = """
                {"email":"%s","username":"ann_profile","password":"%s",
                 "displayName":"Ann","confirmedAdult":true}
                """.formatted(EMAIL, PASSWORD);

        String response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = JsonPath.parse(response).read("$.id", Long.class);

        MockHttpSession session = login(EMAIL, PASSWORD);

        mvc.perform(get("/api/users/" + id + "/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Ann"))
                .andExpect(jsonPath("$.teachSkills", hasSize(0)))
                .andExpect(jsonPath("$.learnSkills", hasSize(0)));
    }

    // same helper as in LoginFlowTests: logs in and returns the session (the cookie)
    private MockHttpSession login(String email, String password) throws Exception {
        return (MockHttpSession) mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }
}