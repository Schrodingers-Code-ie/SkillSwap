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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.UserRepository;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

//@WebMvcTest starts only the web layer (no database), so it runs fast without Docker
@WebMvcTest(MeController.class)
class MeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CurrentUser currentUser;

    @Test
    void returnsCurrentUser() throws Exception {
        User ann = User.restore(1L, new Email("ann@mail.com"), "Ann_S", "hash", Instant.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(ann));
        when(currentUser.getId()).thenReturn(1L);

        mockMvc.perform(get("/api/auth/me"))
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
        when(currentUser.getId()).thenReturn(2L);
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void notLoggedInReturns401() throws Exception {
        when(currentUser.getId()).thenThrow(new UnauthorizedException("Not logged in"));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
