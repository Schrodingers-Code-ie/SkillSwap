package ie.schrodingerscode.skillswap.skill.web;

import ie.schrodingerscode.skillswap.skill.application.ListSkillsService;
import ie.schrodingerscode.skillswap.skill.domain.Skill;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * GET /api/skills, the catalogue the skills popup searches in.
 */
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final ListSkillsService listSkills;

    /**
     * Spring passes in the service.
     *
     * @param listSkills loads the catalogue
     */
    public SkillController(ListSkillsService listSkills) {
        this.listSkills = listSkills;
    }

    /**
     * Lists the catalogue.
     *
     * @return every skill as id and name, sorted by id
     */
    @GetMapping
    public List<Skill> list() {
        return listSkills.listAll();
    }
}
