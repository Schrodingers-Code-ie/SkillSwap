package ie.schrodingerscode.skillswap.auth.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;
import ie.schrodingerscode.skillswap.auth.application.RegisterUser;
import jakarta.transaction.Transactional;

/**
 * Whole app with SecurityConfig and a real Postgres, so this checks login really works end to end.
 * The @WebMvcTest controller tests mock CurrentUser and don't run the security rules.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional // Each test is rolled back, so tests don't affect each other
class LoginFlowTests {

    private static final String PASSWORD = "secret123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterUser registerUser;

    @BeforeEach
    void registerAnn() {
        registerUser.register("ann@mail.com", "annlogin", PASSWORD, "Ann", true);
    }

    @Test
    void loginThenMeReturnsTheUser() throws Exception {
        MockHttpSession session = login("ann@mail.com", PASSWORD);

        mockMvc.perform(get("/api/auth/me").session(session)) //like curl -b jar
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ann@mail.com"));
    }

    @Test
    void meWithoutLoginReturns401Json() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        mockMvc.perform(loginRequest("ann@mail.com", "wrong12345"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Wrong email or password"));
    }

    @Test
    void logoutEndsTheSession() throws Exception {
        MockHttpSession session = login("ann@mail.com", PASSWORD);

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicEndpointsWorkWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/health")).andExpect(status().isOk());
        mockMvc.perform(get("/api/skills")).andExpect(status().isOk());
    }

    //logs in and returns the session, which is what the browser keeps in the cookie
    private MockHttpSession login(String email, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(loginRequest(email, password))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }

    private RequestBuilder loginRequest(String email, String password) {
        return post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");
    }
}
