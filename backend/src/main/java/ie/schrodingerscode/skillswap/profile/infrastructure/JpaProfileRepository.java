package ie.schrodingerscode.skillswap.profile.infrastructure;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
class JpaProfileRepository implements ProfileRepository {

    private final ProfileJpaRepository jpa;

    JpaProfileRepository(ProfileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findById(long userId) {
        return jpa.findById(userId).map(ProfileEntity::toDomain);
    }

    @Override
    @Transactional
    public void save(Profile profile) {
        ProfileEntity entity = jpa.findById(profile.getUserId())
                .orElseGet(() -> new ProfileEntity(profile.getUserId()));
        entity.updateFrom(profile);
        jpa.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> displayNamesOf(Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return jpa.findNames(userIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (String) row[1]));
    }
}
