package ie.schrodingerscode.skillswap.connection.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;

/**
 * TEMPORARY storage: keeps requests in a map, so everything is lost when the backend restarts.
 * TODO: replace with a Postgres version later. Only this class changes, the domain and service stay the same.
 */
@Repository
public class InMemorySwapRequestRepository implements SwapRequestRepository {

    private final Map<Long, SwapRequest> requests = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(); //gives 1, 2, 3... thread safe

    @Override
    public long nextId() {
        return ids.incrementAndGet();
    }

    @Override
    public void save(SwapRequest request) {
        requests.put(request.getId(), request);
    }

    @Override
    public Optional<SwapRequest> findById(long id) {
        return Optional.ofNullable(requests.get(id));
    }

    @Override
    public Optional<SwapRequest> findByPair(UserPair pair) {
        return requests.values().stream()
                .filter(r -> r.pair().equals(pair))
                .findFirst();
    }

    @Override
    public List<SwapRequest> findAllInvolving(long userId) {
        return requests.values().stream()
                .filter(r -> r.involves(userId))
                .toList();
    }

    @Override
    public void delete(SwapRequest request) {
        requests.remove(request.getId());
    }
}
