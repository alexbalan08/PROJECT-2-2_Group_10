package backend.recognition.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTemplateReader {

    private final String fileURL;
    private final List<SkillTemplate> skillTemplates;

    public SkillTemplateReader(String url) {
        this.fileURL = url;
        this.skillTemplates = new ArrayList<>();
    }

    public void readFile() {
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(this.fileURL))) {
            String line;

            List<String> questions = new ArrayList<>();
            Map<String, String> actions = new HashMap<>();
            String answer = "";
            String error = "";

            while ((line = br.readLine()) != null) {
                if(line.startsWith("Question :")) {
                    questions.add(removeKeyWord(line));
                } else if(line.startsWith("Action :")) {
                    String key = line.substring(line.indexOf(" ") + 2, line.lastIndexOf(":"));
                    String value = line.substring(line.lastIndexOf(":") + 2);
                    actions.put(key, value);
                } else if(line.startsWith("Answer :")) {
                    answer = removeKeyWord(line);
                } else if(line.startsWith("Error :")) {
                    error = removeKeyWord(line);
                } else {
                    this.skillTemplates.add(new SkillTemplate(questions, actions, answer, error));
                    questions = new ArrayList<>();
                    actions = new HashMap<>();
                    answer = "";
                    error = "";
                }
            }
        } catch (IOException e) {
            System.out.println("Error with the file : " + this.fileURL);
        }
    }

    public List<SkillTemplate> getSkillTemplates() {
        return this.skillTemplates;
    }

    private String removeKeyWord(String line) {
        int index = line.lastIndexOf(":");
        return line.substring(index + 2);
    }

}
