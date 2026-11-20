package ie.schrodingerscode.skillswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * One STOMP-over-WebSocket connection per browser tab
 * Send to one user with SimpMessagingTemplate.convertAndSendToUser(userId, "/queue/...", payload).
 * The user name is the user ID as a string, because LoginController makes the ID the principal.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TaskScheduler messageBrokerTaskScheduler;

    public WebSocketConfig(@Lazy TaskScheduler messageBrokerTaskScheduler) {
        this.messageBrokerTaskScheduler = messageBrokerTaskScheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(messageBrokerTaskScheduler);
        registry.setUserDestinationPrefix("/user");
    }
}
