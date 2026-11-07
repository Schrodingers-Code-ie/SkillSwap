package ie.schrodingerscode.skillswap.chat.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.chat.domain.Message;
import ie.schrodingerscode.skillswap.chat.domain.MessageText;
import ie.schrodingerscode.skillswap.chat.infrastructure.InMemoryMessageRepository;

class ListChatsTests {

    /** The logged-in user in every test. */
    private static final long ME = 1;
    private final InMemoryMessageRepository repo = new InMemoryMessageRepository();
    private final Instant t0 = Instant.parse("2026-01-01T10:00:00Z");
    /** Profile lookup, without User 20 */
    private final Map<Long, String> names = Map.of(
            5L, "Aoife Wall",
            9L, "ben Ryan", // ignores case
            12L, "Cara Doyle");

    private ListChats withConnections(Long... friendIds) {
        return new ListChats(repo,
                userId -> userId == ME ? List.of(friendIds) : List.of(),
                ids -> names);
    }

    private Message send(long from, long to, String text, Instant at) {
        return repo.save(Message.create(from, to, new MessageText(text), at));
    }

    /** The chat with the newest message comes first, whoever sent that message. */
    @Test
    void newestChatComesFirst() {
        send(ME, 5, "hi Aoife", t0);
        send(5, ME, "hi back", t0.plusSeconds(10)); // newest with Aoife, sent by her
        send(9, ME, "hey", t0.plusSeconds(5));
        send(ME, 9, "my reply to Ben", t0.plusSeconds(20)); // newest overall, sent by me

        List<ChatSummary> chats = withConnections(5L, 9L).execute(ME);

        assertThat(chats).extracting(ChatSummary::userId).containsExactly(9L, 5L);
        assertThat(chats.get(0).lastMessage().text().value()).isEqualTo("my reply to Ben");
        assertThat(chats.get(1).lastMessage().text().value()).isEqualTo("hi back");
    }

    /** Two last messages at exactly the same time: the one saved later (higher id) comes first. */
    @Test
    void sameTimeHigherIdComesFirst() {
        send(ME, 5, "first saved", t0);
        send(ME, 9, "second saved", t0);

        assertThat(withConnections(5L, 9L).execute(ME)).extracting(ChatSummary::userId).containsExactly(9L, 5L);
    }

    /** Connections without messages are still included, after the others, A to Z, ignoring case. */
    @Test
    void connectionsWithoutMessagesComeLastByName() {
        send(ME, 12, "hi Cara", t0);

        List<ChatSummary> chats = withConnections(9L, 12L, 5L).execute(ME); // given out of order on purpose

        assertThat(chats).extracting(ChatSummary::userId).containsExactly(12L, 5L, 9L); // Cara, then Aoife, ben
        assertThat(chats.get(1).lastMessage()).isNull();
        assertThat(chats.get(2).lastMessage()).isNull();
    }

    /** A connection without a profile has a null name and goes after everyone with a name. */
    @Test
    void connectionWithoutNameGoesLast() {
        List<ChatSummary> chats = withConnections(20L, 5L).execute(ME);

        assertThat(chats).extracting(ChatSummary::userId).containsExactly(5L, 20L);
        assertThat(chats.get(1).displayName()).isNull();
    }

    /** Names come from the display-name lookup. */
    @Test
    void entriesHaveDisplayNames() {
        assertThat(withConnections(5L).execute(ME).get(0).displayName()).isEqualTo("Aoife Wall");
    }

    /** Messages with someone who is not a connection do not appear. */
    @Test
    void messagesWithNonConnectionsAreLeftOut() {
        send(ME, 77, "to a stranger", t0.plusSeconds(100)); // newest message, but 77 is not a connection

        List<ChatSummary> chats = withConnections(5L).execute(ME);

        assertThat(chats).extracting(ChatSummary::userId).containsExactly(5L);
        assertThat(chats.get(0).lastMessage()).isNull();
    }

    /** Conversations between two other people never show up as my last message. */
    @Test
    void otherPeoplesConversationsAreLeftOut() {
        send(5, 9, "not my chat", t0);

        List<ChatSummary> chats = withConnections(5L, 9L).execute(ME);

        assertThat(chats).allSatisfy(chat -> assertThat(chat.lastMessage()).isNull());
    }

    /** No connections gives an empty list, not an error. */
    @Test
    void noConnectionsGivesEmptyList() {
        send(ME, 77, "to a stranger", t0);

        assertThat(withConnections().execute(ME)).isEmpty();
    }
}
