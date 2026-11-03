package ie.schrodingerscode.skillswap.profile.infrastructure;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {

    // only the two columns we need, so the skill lists are not loaded. Each row is [userId, fullName]
    @Query("select p.userId, p.fullName from ProfileEntity p where p.userId in :ids")
    List<Object[]> findNames(@Param("ids") Collection<Long> ids);
}
