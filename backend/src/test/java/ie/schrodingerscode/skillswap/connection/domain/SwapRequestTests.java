package ie.schrodingerscode.skillswap.connection.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.connection.domain.exception.InvalidRequestStateException;
import ie.schrodingerscode.skillswap.connection.domain.exception.NotRequestReceiverException;
import ie.schrodingerscode.skillswap.connection.domain.exception.SelfRequestException;

//plain unit tests for the state machine, no Spring, so they run instantly
class SwapRequestTests {

    //user 1 sends to user 2, user 3 is a stranger
    private SwapRequest newRequest() {
        return SwapRequest.create(1, 1, 2);
    }

    @Test
    void newRequestIsPending() {
        assertThat(newRequest().getStatus()).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    void cannotSendRequestToYourself() {
        assertThatThrownBy(() -> SwapRequest.create(1, 5, 5))
                .isInstanceOf(SelfRequestException.class);
    }

    @Test
    void receiverCanAccept() {
        SwapRequest request = newRequest();
        request.accept(2);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    void senderCannotAccept() {
        assertThatThrownBy(() -> newRequest().accept(1))
                .isInstanceOf(NotRequestReceiverException.class);
    }

    @Test
    void strangerCannotAccept() {
        assertThatThrownBy(() -> newRequest().accept(3))
                .isInstanceOf(NotRequestReceiverException.class);
    }

    @Test
    void cannotAcceptTwice() {
        SwapRequest request = newRequest();
        request.accept(2);
        assertThatThrownBy(() -> request.accept(2))
                .isInstanceOf(InvalidRequestStateException.class);
    }

    @Test
    void strangerStillGets403AfterAccept() {
        //a stranger shouldn't find out the request was already accepted
        SwapRequest request = newRequest();
        request.accept(2);
        assertThatThrownBy(() -> request.accept(3))
                .isInstanceOf(NotRequestReceiverException.class);
    }

    @Test
    void senderCannotDecline() {
        assertThatThrownBy(() -> newRequest().decline(1))
                .isInstanceOf(NotRequestReceiverException.class);
    }

    @Test
    void strangerCannotDecline() {
        assertThatThrownBy(() -> newRequest().decline(3))
                .isInstanceOf(NotRequestReceiverException.class);
    }

    @Test
    void cannotDeclineAcceptedRequest() {
        SwapRequest request = newRequest();
        request.accept(2);
        assertThatThrownBy(() -> request.decline(2))
                .isInstanceOf(InvalidRequestStateException.class);
    }

    @Test
    void userPairIsTheSameInBothDirections() {
        assertThat(UserPair.of(1, 2)).isEqualTo(UserPair.of(2, 1));
        assertThat(UserPair.of(1, 2)).isNotEqualTo(UserPair.of(1, 3));
    }
}
