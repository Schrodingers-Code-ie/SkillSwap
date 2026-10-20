package ie.schrodingerscode.skillswap.connection.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;

/**
 * Test-only storage in a map, so SwapRequestServiceTests can run as plain JUnit without a database.
 * The app itself uses JpaSwapRequestRepository. This one has no @Repository on purpose,
 * otherwise Spring would find two SwapRequestRepository beans.
 */
public class InMemorySwapRequestRepository implements SwapRequestRepository {

    private final Map<Long, SwapRequest> requests = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(); //gives 1, 2, 3... like Postgres would

    @Override
    public SwapRequest save(SwapRequest request) {
        SwapRequest saved = request.getId() == null
                ? SwapRequest.restore(ids.incrementAndGet(), request.getSenderId(), request.getReceiverId(),
                        request.getStatus(), request.getCreatedAt())
                : request;
        requests.put(saved.getId(), saved);
        return saved;
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
