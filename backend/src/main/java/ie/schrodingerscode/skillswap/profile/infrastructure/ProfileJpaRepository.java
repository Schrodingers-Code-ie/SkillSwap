package ie.schrodingerscode.skillswap.profile.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {
}
