package ie.schrodingerscode.skillswap.profile.infrastructure;

import ie.schrodingerscode.skillswap.profile.domain.Profile;
import ie.schrodingerscode.skillswap.profile.domain.ProfileRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stores profiles in the profiles, user_teach_skills and user_learn_skills tables through JPA.
 * Package-private, so the rest of the app only sees ProfileRepository.
 */
@Repository
class JpaProfileRepository implements ProfileRepository {

    private final ProfileJpaRepository jpa;

    /**
     * Spring passes in the generated Spring Data repository.
     *
     * @param jpa reads and writes the profiles table
     */
    JpaProfileRepository(ProfileJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * Loads the profile row and its two skill tables.
     *
     * @param userId the user whose profile to load
     * @return the profile, or empty if there's no row for this user
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findById(long userId) {
        return jpa.findById(userId).map(ProfileEntity::toDomain);
    }

    /**
     * Loads the existing row first, so JPA updates it instead of inserting a second one,
     * then replaces both skill lists with the ones in the profile.
     *
     * @param profile the profile to store
     */
    @Override
    @Transactional
    public void save(Profile profile) {
        ProfileEntity entity = jpa.findById(profile.getUserId())
                .orElseGet(() -> new ProfileEntity(profile.getUserId()));
        entity.updateFrom(profile);
        jpa.save(entity);
    }

    /**
     * Loads only the id and name columns, so the skill tables aren't read at all.
     *
     * @param userIds the users to look up
     * @return user id to display name. Ids without a profile are left out
     */
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
