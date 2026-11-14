package ie.schrodingerscode.skillswap.block.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.block.application.BlockService;
import ie.schrodingerscode.skillswap.block.domain.exception.AlreadyBlockedException;

//controller only, the service is mocked
@WebMvcTest(BlockController.class)
@AutoConfigureMockMvc(addFilters = false)
class BlockControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlockService service;

    @MockitoBean
    private CurrentUser currentUser;

    @Test
    void blockingReturns204() throws Exception {
        when(currentUser.getId()).thenReturn(1L);
        mockMvc.perform(post("/api/blocks/2")).andExpect(status().isNoContent());
        verify(service).block(1, 2);
    }

    @Test
    void blockingTwiceReturns409() throws Exception {
        when(currentUser.getId()).thenReturn(1L);
        when(service.block(1, 2)).thenThrow(new AlreadyBlockedException());
        mockMvc.perform(post("/api/blocks/2")).andExpect(status().isConflict());
    }

    @Test
    void unblockingReturns204() throws Exception {
        when(currentUser.getId()).thenReturn(1L);
        mockMvc.perform(delete("/api/blocks/2")).andExpect(status().isNoContent());
        verify(service).unblock(1, 2);
    }
}
