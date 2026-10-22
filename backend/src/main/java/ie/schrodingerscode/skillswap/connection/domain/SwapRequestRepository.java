package ie.schrodingerscode.skillswap.connection.domain;

import java.util.List;
import java.util.Optional;

/**
 * Where swap requests are stored. No Spring or JPA in here on purpose:
 * the Postgres version lives in infrastructure, and the domain doesn't know about it.
 */
public interface SwapRequestRepository {

    //returns the saved request, a new one comes back with the id Postgres gave it
    SwapRequest save(SwapRequest request);

    Optional<SwapRequest> findById(long id);

    //any request (pending or accepted) between these two users, in either direction
    Optional<SwapRequest> findByPair(UserPair pair);

    //every request where the user is the sender or the receiver
    List<SwapRequest> findAllInvolving(long userId);

    void delete(SwapRequest request);
}
