package ie.schrodingerscode.skillswap.skill.infrastructure;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "skills")
class SkillEntity {
    @Id private Long id;
    @Column(nullable = false) private String name;

    protected SkillEntity() {}

    Skill toDomain() { return new Skill(id, name); }
}