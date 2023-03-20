package backend.recognition.user;

import backend.recognition.SkillRecognition;

import java.util.Objects;

/*
 * Which lectures are there on Monday at 9 ?
 * What is the capital of Belgium ?
 * Which transport do I take to go to Liege ?
 * */

public class UserSkillRecognition implements SkillRecognition {

    private final String fileURL = "./src/main/java/backend/Skills/SkillsTemplate.txt";
    private final SkillTemplateReader reader;

    public UserSkillRecognition() {
        this.reader = new SkillTemplateReader(this.fileURL);
        reader.readFile();
    }

    @Override
    public String determineSkill(String input) {
        for(SkillTemplate skill : this.reader.getSkillTemplates()) {
            String answer = skill.findAnswer(input);
            if(!Objects.equals(answer, "")) {
                return answer;
            }
        }
        return "";
    }


}
