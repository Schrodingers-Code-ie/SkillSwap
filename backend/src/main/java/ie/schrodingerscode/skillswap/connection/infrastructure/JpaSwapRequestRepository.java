package ie.schrodingerscode.skillswap.connection.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ie.schrodingerscode.skillswap.connection.domain.SwapRequest;
import ie.schrodingerscode.skillswap.connection.domain.SwapRequestRepository;
import ie.schrodingerscode.skillswap.connection.domain.UserPair;

/**
 * Stores swap requests in Postgres (the swap_requests table).
 * Turns SwapRequest into SwapRequestEntity and back, so the domain and service don't change.
 */
@Repository
public class JpaSwapRequestRepository implements SwapRequestRepository {

    private final SwapRequestJpaRepository jpa;

    public JpaSwapRequestRepository(SwapRequestJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public SwapRequest save(SwapRequest request) {
        return jpa.save(SwapRequestEntity.from(request)).toDomain();
    }

    @Override
    public Optional<SwapRequest> findById(long id) {
        return jpa.findById(id).map(SwapRequestEntity::toDomain);
    }

    @Override
    public Optional<SwapRequest> findByPair(UserPair pair) {
        return jpa.findByUsers(pair.first(), pair.second()).map(SwapRequestEntity::toDomain);
    }

    @Override
    public List<SwapRequest> findAllInvolving(long userId) {
        return jpa.findBySenderIdOrReceiverId(userId, userId).stream()
                .map(SwapRequestEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(SwapRequest request) {
        jpa.deleteById(request.getId());
    }
}
