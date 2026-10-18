package ie.schrodingerscode.skillswap.profile.infrastructure;

import ie.schrodingerscode.skillswap.profile.domain.Proficiency;
import ie.schrodingerscode.skillswap.profile.domain.Profile;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles")
class ProfileEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String bio;

    @ElementCollection
    @CollectionTable(name = "user_teach_skills", joinColumns = @JoinColumn(name = "user_id"))
    private Set<TeachSkillRow> teachSkills = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_learn_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill_id")
    private Set<Long> learnSkillIds = new HashSet<>();

    protected ProfileEntity() {}

    ProfileEntity(Long userId) {
        this.userId = userId;
    }

    Profile toDomain() {
        Profile profile = new Profile(userId, fullName, bio);
        teachSkills.stream()
                .sorted(Comparator.comparingLong(TeachSkillRow::skillId))
                .forEach(t -> profile.addTeachSkill(t.skillId(), t.proficiency()));
        learnSkillIds.stream().sorted().forEach(profile::addLearnSkill);
        return profile;
    }

    void updateFrom(Profile profile) {
        this.fullName = profile.getDisplayName();
        this.bio = profile.getBio();
        this.teachSkills.clear();
        profile.getTeachSkills().forEach(t ->
                this.teachSkills.add(new TeachSkillRow(t.skillId(), t.proficiency())));
        this.learnSkillIds.clear();
        this.learnSkillIds.addAll(profile.getLearnSkillIds());
    }

    @Embeddable
    record TeachSkillRow(
            @Column(name = "skill_id") long skillId,
            @Enumerated(EnumType.STRING) @Column(name = "proficiency") Proficiency proficiency) {}
}