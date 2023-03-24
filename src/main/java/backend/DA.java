package backend;

import backend.Skills.*;
import backend.recognition.LanguageModel;
import backend.recognition.SkillRecognition;
import backend.recognition.SlotRecognition;
import backend.recognition.api.*;
import backend.recognition.user.SkillTemplate;
import backend.recognition.user.SkillTemplateReader;
import backend.recognition.user.UserSkillRecognition;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DA implements ActionQuery {

    private final Map<SkillWrapper, SlotRecognition> skills;
    private final SkillRecognition apiSkillRecognition;
    private SkillRecognition userSkillRecognition;
    
    private final SkillTemplateReader fileReader;
    private final LanguageModel languageModel;
    private List<SkillTemplate> skillTemplates;
    private final SkillEditor skillEditor;

    public DA() throws Exception {
        this.skills = new HashMap<>();
        this.addSkill(new Weather(), new WeatherSlotRecognition());
        this.addSkill(new Spotify(), new SpotifySlotRecognition());
        this.addSkill(new Canvas(), new CanvasSlotRecognition());
        this.addSkill(new Wikipedia(), new WikipediaSlotRecognition());

        this.fileReader = new SkillTemplateReader("./src/main/java/backend/Skills/SkillsTemplate.txt");
        this.skillTemplates = fileReader.getSkillTemplates();

        this.apiSkillRecognition = new ApiSkillRecognition();
        this.userSkillRecognition = new UserSkillRecognition(this.skillTemplates);

        this.languageModel = new LanguageModel();
        this.skillEditor = new SkillEditor();
    }

    private void addSkill(SkillWrapper skill, SlotRecognition slotRecognition) {
        this.skills.put(skill, slotRecognition);
        System.out.println(skill.getClass().getSimpleName() + " wrapper loaded successfully!");
    }

    public String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException {
        this.skillEditor.setQuery(query);
        if (this.skillEditor.isQueryToEditSkill()) {
            String answer = this.skillEditor.startQuery(query);
            this.skillTemplates = fileReader.getSkillTemplates();
            this.userSkillRecognition = new UserSkillRecognition(this.skillTemplates);
            return answer;
        } else {
            query = query.replace("\n", "").trim();
            return doSkill(query);
        }
    }

    /**
     *
     * It takes in a user's query as input, and then tries to determine which skill to use to respond to the query.
     * The method first checks if the query matches any user-defined skills.
     * If not, it checks if the query matches any pre-defined API skills.
     * If it still can't determine the appropriate skill, it uses the language model to try to determine which skill to use.
     *
     * Once the appropriate skill has been determined, the method uses the skill's findSlot method to extract any necessary information from the query.
     * It then calls the skill's start method to execute the skill's logic, and finally returns the response generated by the skill.
     *
     * If it is unable to determine an appropriate skill to use, it returns a default "Sorry, I didn't understand you ..." response.
     * If an exception is thrown during the processing of the query, the method catches the exception and returns a generic error message.
     *
     * */
    private String doSkill(String query) throws IOException {
        StringBuilder output = new StringBuilder();

        try {
            // CHECK IF IT'S A SKILL ADDED BY THE USER (in a file)
            output.append(this.userSkillRecognition.determineSkill(query));

            // THEN CHECK IF IT'S AN API SKILL WANTED
            if (output.isEmpty()) {
                String determinedSkill = this.apiSkillRecognition.determineSkill(query.toLowerCase(Locale.ROOT));
                for (SkillWrapper skill : this.skills.keySet()) {
                    if (skill.getClass().getSimpleName().equals(determinedSkill)) {
                        List<String> slots = this.skills.get(skill).findSlot(query);
                        skill.start(slots);
                        output.append(skill.getResponse());
                        break;
                    }
                }
            }

            if (output.isEmpty()) {
                String determinedSkill = languageModel.determineSkill(query);
                if (determinedSkill.equals("Random"))
                    return "Sorry, I'm not sure I understood ...";
                for (SkillWrapper skill : this.skills.keySet()) {
                    if (skill.getClass().getSimpleName().equals(determinedSkill)) {
                        List<String> slots = languageModel.findSlot(query);
                        output.append(languageModel.botResponse(slots));
                        if (slots != null && slots.get(0) != null) {
                            skill.start(slots);
                            output.append(skill.getResponse());
                        }
                        break;
                    }
                }
            }

            if (!output.isEmpty()) {
                return output.toString();
            } else {
                return "Sorry, I didn't understand you ...";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, that confused me ...";
        }

    }
}
