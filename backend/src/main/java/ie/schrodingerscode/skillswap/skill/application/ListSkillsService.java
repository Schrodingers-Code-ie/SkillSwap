package ie.schrodingerscode.skillswap.skill.application;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gives the frontend the whole catalogue for the search in the skills popup.
 * It's small (25 skills), so there's no paging and the filtering happens in the browser.
 */
@Service
public class ListSkillsService {

    private final SkillRepository skills;

    /**
     * Spring passes in the skill repository.
     *
     * @param skills where the catalogue is read from
     */
    public ListSkillsService(SkillRepository skills) {
        this.skills = skills;
    }

    /**
     * All skills in the catalogue.
     *
     * @return every skill, sorted by id
     */
    public List<Skill> listAll() {
        return skills.findAll();
    }
}
