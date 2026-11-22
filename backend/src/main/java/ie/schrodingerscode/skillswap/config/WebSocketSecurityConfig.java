package ie.schrodingerscode.skillswap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;

/**
 * Security for STOMP frames after the {@code /ws} handshake (the handshake itself is protected in
 * SecurityConfig).
 *
 * <p>Guarantees that a client can only subscribe to its own {@code /user/queue/...} destinations, so
 * nobody can listen to another user's messages, and that CONNECT carries the CSRF token. Clients
 * can't SEND over the socket at all; sending a message stays on the REST POST.
 */
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    /**
     * Rules for each kind of STOMP frame, checked top to bottom:
     * <ul>
     *   <li>frames with no destination (CONNECT, UNSUBSCRIBE, DISCONNECT, heartbeats): logged-in users;</li>
     *   <li>SUBSCRIBE to {@code /user/queue/**}: logged-in users;</li>
     *   <li>everything else (subscribing straight to {@code /queue/...}, or SEND): denied.</li>
     * </ul>
     */
    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/**").authenticated()
                .anyMessage().denyAll();
        return messages.build();
    }

    /**
     * Checks the CSRF token on CONNECT. A bean with this name replaces the default interceptor of
     * {@code @EnableWebSocketSecurity}, which expects a masked token: the frontend sends the raw token
     * from the XSRF-TOKEN cookie, the same as for REST requests.
     */
    @Bean
    ChannelInterceptor csrfChannelInterceptor() {
        return new CsrfChannelInterceptor();
    }
}
