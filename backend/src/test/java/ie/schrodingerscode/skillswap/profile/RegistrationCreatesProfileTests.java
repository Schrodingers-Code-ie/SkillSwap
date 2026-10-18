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
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class RegistrationCreatesProfileTests {

    @Autowired
    MockMvc mvc;

    @Test
    void registeredUserHasEmptyProfile() throws Exception {
        String body = """
                {"email":"ann.profile@mail.com","username":"ann_profile","password":"secret123",
                 "displayName":"Ann","confirmedAdult":true}
                """;

        String response = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = JsonPath.parse(response).read("$.id", Long.class);

        mvc.perform(get("/api/users/" + id + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Ann"))
                .andExpect(jsonPath("$.teachSkills", hasSize(0)))
                .andExpect(jsonPath("$.learnSkills", hasSize(0)));
    }
}
