package ie.schrodingerscode.skillswap.skill.application;

import ie.schrodingerscode.skillswap.skill.domain.Skill;
import ie.schrodingerscode.skillswap.skill.domain.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListSkillsService {

    private final SkillRepository skills;

    public ListSkillsService(SkillRepository skills) {
        this.skills = skills;
    }

    public List<Skill> listAll() {
        return skills.findAll();
    }
}