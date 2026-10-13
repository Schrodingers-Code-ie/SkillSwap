package ie.schrodingerscode.skillswap.auth.web;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import ie.schrodingerscode.skillswap.auth.application.RegisterUser;
import ie.schrodingerscode.skillswap.auth.domain.Email;
import ie.schrodingerscode.skillswap.auth.domain.User;
import ie.schrodingerscode.skillswap.auth.domain.exception.AgeNotConfirmedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.EmailAlreadyUsedException;
import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException;

/**
 * Web layer tests with RegisterUser mocked, as in STRUCTURE.md.
 * These only check JSON in/out and status codes. The rules are tested in UserTests and RegisterUserTests.
 */
@WebMvcTest(RegisterController.class)
class RegisterControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUser registerUser;

    private ResultActions register(String json) throws Exception {
        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private static final String VALID_BODY = """
            {"email": "ann@mail.com", "password": "secret123", "displayName": "Ann", "confirmedAdult": true}
            """;

    @Test
    void validRegistrationReturns201WithoutPassword() throws Exception {
        User saved = User.register(new Email("ann@mail.com"), "hash", "Ann", true).withId(1);
        when(registerUser.register("ann@mail.com", "secret123", "Ann", true)).thenReturn(saved);

        register(VALID_BODY)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ann@mail.com"))
                .andExpect(jsonPath("$.displayName").value("Ann"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void missingFieldsReturn400WithoutCallingService() throws Exception {
        register("""
                {"email": "ann@mail.com", "password": "secret123", "displayName": "Ann"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("confirmedAdult: must not be null"));

        register("""
                {"email": "", "password": "secret123", "displayName": "Ann", "confirmedAdult": true}
                """)
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registerUser);
    }

    @Test
    void notConfirmedAdultReturns400() throws Exception {
        when(registerUser.register(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new AgeNotConfirmedException("You must confirm you are 18 or older"));

        register(VALID_BODY)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You must confirm you are 18 or older"));
    }

    @Test
    void invalidEmailReturns400() throws Exception {
        when(registerUser.register(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new InvalidEmailException("Email format is incorrect"));

        register(VALID_BODY)
                .andExpect(status().isBadRequest());
    }

    @Test
    void emailAlreadyUsedReturns409() throws Exception {
        when(registerUser.register(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new EmailAlreadyUsedException("Email is already used"));

        register(VALID_BODY)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
