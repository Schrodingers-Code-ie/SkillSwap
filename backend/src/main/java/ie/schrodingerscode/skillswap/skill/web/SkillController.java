package ie.schrodingerscode.skillswap.skill.web;

import ie.schrodingerscode.skillswap.skill.application.ListSkillsService;
import ie.schrodingerscode.skillswap.skill.domain.Skill;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final ListSkillsService listSkills;

    public SkillController(ListSkillsService listSkills) {
        this.listSkills = listSkills;
    }

    @GetMapping
    public List<Skill> list() {
        return listSkills.listAll();
    }
}
