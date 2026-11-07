package ie.schrodingerscode.skillswap.chat.web;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ie.schrodingerscode.skillswap.auth.CurrentUser;
import ie.schrodingerscode.skillswap.chat.application.ChatSummary;
import ie.schrodingerscode.skillswap.chat.application.ListChats;
import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.common.exception.UnauthorizedException;

@WebMvcTest(ChatListController.class)
class ChatListControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrentUser currentUser;

    @MockitoBean
    private ListChats listChats;

    @Test
    void returnsChatListInTicketShape() throws Exception {
        Message last = new Message(42L, 5, 1, new MessageText("See you Tuesday"),
                Instant.parse("2026-11-07T10:15:00Z"));
        when(currentUser.getId()).thenReturn(1L);
        when(listChats.execute(1L)).thenReturn(List.of(
                new ChatSummary(5, "Aoife Wall", last),
                new ChatSummary(9, "Ben Ryan", null)));

        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(5))
                .andExpect(jsonPath("$[0].displayName").value("Aoife Wall"))
                .andExpect(jsonPath("$[0].lastMessage.id").value(42))
                .andExpect(jsonPath("$[0].lastMessage.senderId").value(5))
                .andExpect(jsonPath("$[0].lastMessage.text").value("See you Tuesday"))
                .andExpect(jsonPath("$[0].lastMessage.sentAt").value("2026-11-07T10:15:00Z"))
                .andExpect(jsonPath("$[0].lastMessage.receiverId").doesNotExist())
                .andExpect(jsonPath("$[1].userId").value(9))
                .andExpect(jsonPath("$[1].displayName").value("Ben Ryan"))
                .andExpect(jsonPath("$[1]", hasKey("lastMessage")))
                .andExpect(jsonPath("$[1].lastMessage").value(nullValue()));
    }

    /** No connections gives an empty JSON array. */
    @Test
    void noChatsGivesEmptyArray() throws Exception {
        when(currentUser.getId()).thenReturn(1L);
        when(listChats.execute(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /** When nobody is logged in, CurrentUser throws and the request returns 401. */
    @Test
    void notLoggedInReturns401() throws Exception {
        when(currentUser.getId()).thenThrow(new UnauthorizedException("Not logged in"));

        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        verifyNoInteractions(listChats);
    }
}
