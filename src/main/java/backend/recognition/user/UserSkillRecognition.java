package backend.recognition.user;

import backend.recognition.SkillRecognition;

import java.util.List;
import java.util.Objects;

/**
 *
 * This code snippet is a class UserSkillRecognition that implements the SkillRecognition interface.
 * The class takes a list of SkillTemplate objects in its constructor.
 *
 * */

public class UserSkillRecognition implements SkillRecognition {

    private final List<SkillTemplate> skillTemplates;

    public UserSkillRecognition(List<SkillTemplate> list) {
        this.skillTemplates = list;
    }

    /**
     *
     * The determineSkill method is implemented in the class to find the skill required for a particular task or problem based on the input text.
     *
     * The method iterates over the list of SkillTemplate objects passed to the constructor and calls the findAnswer method of each SkillTemplate to determine if the input text matches the template for a particular skill.
     * If a match is found, the method returns the answer associated with that skill. If no match is found, an empty string is returned.
     *
     * */
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
