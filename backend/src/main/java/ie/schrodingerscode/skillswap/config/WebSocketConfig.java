package ie.schrodingerscode.skillswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Sets up STOMP over WebSocket at {@code /ws}, one connection per browser tab, used to push live
 * updates (new messages, new connections) from the server to the browser.
 *
 * <p>To send to one user, use
 * {@code SimpMessagingTemplate.convertAndSendToUser(userId, "/queue/...", payload)}. The user name is
 * the user id as a string, because LoginController makes the id the principal. The browser
 * subscribes to {@code /user/queue/...}, which Spring turns into that user's own private queue.
 *
 * <p>The broker sends and expects a heartbeat every 10 seconds, so a dead connection is noticed and
 * the frontend reconnects.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TaskScheduler messageBrokerTaskScheduler;

    /**
     * @param messageBrokerTaskScheduler scheduler for the heartbeats; {@code @Lazy} because Spring creates
     *                                   it from this same configuration
     */
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
