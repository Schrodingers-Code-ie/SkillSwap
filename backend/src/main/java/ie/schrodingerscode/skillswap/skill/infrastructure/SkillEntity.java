package ie.schrodingerscode.skillswap.skill.infrastructure;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA mapping of a row in skills. There are no setters because the app never changes skills.
 */
@Entity
@Table(name = "skills")
class SkillEntity {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;

    protected SkillEntity() {
    }

    /**
     * Turns this database row into the domain record the rest of the app uses.
     *
     * @return the same id and name as a {@link Skill}
     */
    Skill toDomain() {
        return new Skill(id, name);
    }
}
