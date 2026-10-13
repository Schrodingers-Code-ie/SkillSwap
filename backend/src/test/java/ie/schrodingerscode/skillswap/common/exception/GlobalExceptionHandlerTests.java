package ie.schrodingerscode.skillswap.common.exception;

import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@WebMvcTest starts only the web layer (no database), so it runs fast without Docker
@WebMvcTest(GlobalExceptionHandlerTests.TestController.class)
@Import(GlobalExceptionHandlerTests.TestController.class) //@WebMvcTest skips classes nested in tests, so add it by hand
class GlobalExceptionHandlerTests {

    //small controller that only exists in this test, instead of a temporary one
    @RestController
    @RequestMapping("/api/test")
    static class TestController {

        @GetMapping("/param")
        public String param(@RequestParam("name") String name) {
            return "ok " + name;
        }

        @PostMapping("/body")
        public String body(@RequestBody Map<String, String> body) {
            return "ok";
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void missingParamReturns400() throws Exception {
        mockMvc.perform(get("/api/test/param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required parameter 'name'"));
    }

    @Test
    void paramGivenReturns200() throws Exception {
        mockMvc.perform(get("/api/test/param").param("name", "chris"))
                .andExpect(status().isOk());
    }

    @Test
    void wrongContentTypeReturns415() throws Exception {
        mockMvc.perform(post("/api/test/body").contentType(MediaType.TEXT_PLAIN).content("hello"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("Content type not supported, send the body as application/json"));
    }
}
