package ie.schrodingerscode.skillswap.connection.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.exception.DuplicateRequestException;
import ie.schrodingerscode.skillswap.connection.domain.exception.RequestNotFoundException;
import ie.schrodingerscode.skillswap.connection.infrastructure.InMemorySwapRequestRepository;

//uses the real in-memory repository, still no Spring needed
class SwapRequestServiceTest {

    private SwapRequestService service;

    @BeforeEach
    void setUp() {
        service = new SwapRequestService(new InMemorySwapRequestRepository()); //fresh empty storage for every test
    }

    @Test
    void secondRequestSameDirectionIsRejected() {
        service.send(1, 2);
        assertThatThrownBy(() -> service.send(1, 2))
                .isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    void secondRequestOtherDirectionIsRejected() {
        service.send(1, 2);
        assertThatThrownBy(() -> service.send(2, 1))
                .isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    void cannotRequestSomeoneYouAreAlreadyConnectedTo() {
        SwapRequest request = service.send(1, 2);
        service.accept(request.getId(), 2);
        assertThatThrownBy(() -> service.send(2, 1))
                .isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    void acceptingConnectsBothUsers() {
        SwapRequest request = service.send(1, 2);
        service.accept(request.getId(), 2);

        assertThat(service.areConnected(1, 2)).isTrue();
        assertThat(service.areConnected(2, 1)).isTrue();
        assertThat(service.connections(1)).hasSize(1);
        assertThat(service.connections(2)).hasSize(1);
        assertThat(service.pendingRequests(1)).isEmpty();
    }

    @Test
    void pendingRequestIsNotAConnectionYet() {
        service.send(1, 2);
        assertThat(service.areConnected(1, 2)).isFalse();
        assertThat(service.pendingRequests(1)).hasSize(1); //outgoing for user 1
        assertThat(service.pendingRequests(2)).hasSize(1); //incoming for user 2
        assertThat(service.pendingRequests(3)).isEmpty();
    }

    @Test
    void decliningDeletesTheRequest() {
        SwapRequest request = service.send(1, 2);
        service.decline(request.getId(), 2);

        assertThat(service.pendingRequests(1)).isEmpty();
        assertThat(service.pendingRequests(2)).isEmpty();
        //deleted, so they can try again later
        service.send(2, 1);
    }

    @Test
    void unknownRequestIsNotFound() {
        assertThatThrownBy(() -> service.accept(999, 2))
                .isInstanceOf(RequestNotFoundException.class);
        assertThatThrownBy(() -> service.decline(999, 2))
                .isInstanceOf(RequestNotFoundException.class);
    }
}
