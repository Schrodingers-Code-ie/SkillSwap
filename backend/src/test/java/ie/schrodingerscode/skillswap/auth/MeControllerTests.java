package ie.schrodingerscode.skillswap.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

//@WebMvcTest starts only the web layer (no database), so it runs fast without Docker
@WebMvcTest(MeController.class)
@Import(HeaderCurrentUser.class) //@WebMvcTest skips @Component classes, so add the stub by hand
class MeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsIdFromHeader() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/api/auth/me").header("X-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void missingHeaderReturns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void invalidHeaderReturns401() throws Exception {
        for (String bad : new String[] { "abc", "0", "-5", " " }) {
            mockMvc.perform(get("/api/auth/me").header("X-User-Id", bad))
                    .andExpect(status().isUnauthorized());
        }
    }
}
