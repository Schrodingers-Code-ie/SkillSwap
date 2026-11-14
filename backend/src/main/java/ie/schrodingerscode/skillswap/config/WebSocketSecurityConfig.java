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
 * Security for STOMP frames after the /ws handshake (the handshake itself is protected in SecurityConfig).
 * Clients may only subscribe to their own /user/queue/... destinations, and CONNECT needs the CSRF token.
 */
@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                //CONNECT, UNSUBSCRIBE, DISCONNECT and heartbeats have no destination
                .nullDestMatcher().authenticated()
                //only through /user/..., which spring turns into this connection's own private queue
                .simpSubscribeDestMatchers("/user/queue/**").authenticated()
                //everything else: subscribing straight to /queue/..., or SEND (sending stays on POST)
                .anyMessage().denyAll();
        return messages.build();
    }

    @Bean
    ChannelInterceptor csrfChannelInterceptor() {
        return new CsrfChannelInterceptor();
    }
}