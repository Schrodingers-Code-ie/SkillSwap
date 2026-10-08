package ie.schrodingerscode.skillswap.connection.domain;

import java.util.List;
import java.util.Optional;

/**
 * Where swap requests are stored. No Spring or JPA in here on purpose:
 * right now it's an in-memory map, later it can be Postgres, and the domain doesn't change.
 */
public interface SwapRequestRepository {

    long nextId();

    void save(SwapRequest request);

    Optional<SwapRequest> findById(long id);

    //any request (pending or accepted) between these two users, in either direction
    Optional<SwapRequest> findByPair(UserPair pair);

    //every request where the user is the sender or the receiver
    List<SwapRequest> findAllInvolving(long userId);

    void delete(SwapRequest request);
}
