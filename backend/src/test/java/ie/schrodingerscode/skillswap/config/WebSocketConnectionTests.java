package ie.schrodingerscode.skillswap.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.jayway.jsonpath.JsonPath;

import ie.schrodingerscode.skillswap.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class WebSocketConnectionTests {

    private static final String PASSWORD = "secret123";

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final HttpClient http = HttpClient.newHttpClient();
    private WebSocketStompClient stompClient;

    @BeforeEach
    void createStompClient() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @AfterEach
    void stopStompClient() {
        stompClient.stop();
    }

    @Test
    void loggedInUserReceivesMessageSentToTheirUserDestination() throws Exception {
        LoggedInUser ann = registerAndLogin();
        StompSession session = connect(ann).get(5, TimeUnit.SECONDS);

        BlockingQueue<String> received = new LinkedBlockingQueue<>();
        session.subscribe("/user/queue/test", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.add((String) payload);
            }
        });

        String message = null;
        long deadline = System.currentTimeMillis() + 5000;
        while (message == null && System.currentTimeMillis() < deadline) {
            messagingTemplate.convertAndSendToUser(String.valueOf(ann.id()), "/queue/test", "hello");
            message = received.poll(200, TimeUnit.MILLISECONDS);
        }

        assertThat(message).isEqualTo("hello");
        session.disconnect();
    }

    @Test
    void loggedOutClientCannotConnect() {
        CompletableFuture<StompSession> connecting = connect(null);

        assertThatThrownBy(() -> connecting.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .rootCause()
                .hasMessageContaining("401");
    }

    private CompletableFuture<StompSession> connect(LoggedInUser user) {
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        StompHeaders connectHeaders = new StompHeaders();
        if (user != null) {
            handshakeHeaders.add(HttpHeaders.COOKIE, user.cookies());
            connectHeaders.add("X-XSRF-TOKEN", user.csrfToken());
        }
        return stompClient.connectAsync("ws://localhost:" + port + "/ws", handshakeHeaders, connectHeaders,
                new StompSessionHandlerAdapter() {
                });
    }

    private record LoggedInUser(long id, String cookies, String csrfToken) {
    }

    private LoggedInUser registerAndLogin() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String email = "ws." + suffix + "@mail.com";

        HttpResponse<String> registered = post("/api/auth/register", """
                {"email":"%s","username":"ws_%s","password":"%s",
                 "displayName":"Ws","confirmedAdult":true}
                """.formatted(email, suffix, PASSWORD));
        assertThat(registered.statusCode()).isEqualTo(201);
        long id = JsonPath.parse(registered.body()).read("$.id", Long.class);

        HttpResponse<String> loggedIn = post("/api/auth/login",
                "{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, PASSWORD));
        assertThat(loggedIn.statusCode()).isEqualTo(200);

        String sessionCookie = cookie(loggedIn, "JSESSIONID");
        String csrfCookie = cookie(loggedIn, "XSRF-TOKEN");
        String csrfToken = csrfCookie.substring("XSRF-TOKEN=".length());
        return new LoggedInUser(id, sessionCookie + "; " + csrfCookie, csrfToken);
    }

    private static String cookie(HttpResponse<?> response, String name) {
        return response.headers().allValues(HttpHeaders.SET_COOKIE).stream()
                .filter(setCookie -> setCookie.startsWith(name + "="))
                .map(setCookie -> setCookie.split(";", 2)[0])
                .findFirst()
                .orElseThrow(() -> new AssertionError("No " + name + " cookie in the response"));
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
