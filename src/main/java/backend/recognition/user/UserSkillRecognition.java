package backend.recognition.user;

import backend.recognition.SkillRecognition;

import java.util.List;
import java.util.Objects;

/*
 * Which lectures are there on Monday at 9 ?
 * What is the capital of Belgium ?
 * Which transport do I take to go to Liege ?
 * */

public class UserSkillRecognition implements SkillRecognition {

    private final List<SkillTemplate> skillTemplates;

    public UserSkillRecognition(List<SkillTemplate> list) {
        this.skillTemplates = list;
    }

    @Override
    public String determineSkill(String input) {
        for(SkillTemplate skill : this.skillTemplates) {
            String answer = skill.findAnswer(input);
            if(!Objects.equals(answer, "")) {
                return answer;
            }
        }
        return "";
    }


}
