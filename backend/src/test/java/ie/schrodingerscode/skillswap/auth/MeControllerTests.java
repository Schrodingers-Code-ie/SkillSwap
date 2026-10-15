package ie.schrodingerscode.skillswap.auth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;

//@WebMvcTest starts only the web layer (no database), so it runs fast without Docker
@WebMvcTest(MeController.class)
@Import(HeaderCurrentUser.class) //@WebMvcTest skips @Component classes, so add the stub by hand
class MeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void returnsUserFromHeader() throws Exception {
        User ann = User.restore(1L, new Email("ann@mail.com"), "Ann_S", "hash", Instant.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(ann));

        mockMvc.perform(get("/api/auth/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ann@mail.com"))
                .andExpect(jsonPath("$.username").value("Ann_S"))
                .andExpect(jsonPath("$.displayName").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void unknownUserReturns404() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/me").header("X-User-Id", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingHeaderReturns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void invalidHeaderReturns401() throws Exception {
        for (String bad : new String[]{"abc", "0", "-5", " "}) {
            mockMvc.perform(get("/api/auth/me").header("X-User-Id", bad))
                    .andExpect(status().isUnauthorized());
        }
    }
}
