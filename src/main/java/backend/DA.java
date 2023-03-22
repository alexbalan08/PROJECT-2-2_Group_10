package backend;

import UI.HelloApplication;
import backend.Skills.*;
import backend.recognition.LanguageModel;
import backend.recognition.SkillRecognition;
import backend.recognition.SlotRecognition;
import backend.recognition.api.*;
import backend.recognition.user.UserSkillRecognition;
import javafx.scene.control.TextArea;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DA implements ActionQuery {

    private final Map<SkillWrapper, SlotRecognition> skills;
    private final SkillRecognition apiSkillRecognition;
    private final SkillRecognition userSkillRecognition;
    private final LanguageModel languageModel;

    private SkillEditor skillEditor;

    public DA() throws Exception {
        this.skills = new HashMap<>();
        this.addSkill(new Weather(), new WeatherSlotRecognition());
        this.addSkill(new Spotify(), new SpotifySlotRecognition());
        this.addSkill(new Canvas(), new CanvasSlotRecognition());
        this.addSkill(new Wikipedia(), new WikipediaSlotRecognition());

        this.skillEditor = new SkillEditor();

        this.apiSkillRecognition = new ApiSkillRecognition();
        this.userSkillRecognition = new UserSkillRecognition();
        this.languageModel = new LanguageModel();
    }

    private void addSkill(SkillWrapper skill, SlotRecognition slotRecognition) {
        this.skills.put(skill, slotRecognition);
        System.out.println(skill.getClass().getSimpleName() + " wrapper loaded successfully!");
    }

    public String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException {
        this.skillEditor.setQuery(query);
        if (this.skillEditor.isQueryToEditSkill())
            return this.skillEditor.startQuery(query);
        else {
            query = query.replace("\n", "").trim();
            return doSkill(query);
        }
    }

    private String doSkill(String query) throws IOException {
        StringBuilder output = new StringBuilder();

        // CHECK IF IT'S SKILL EDITOR
        // TODO (or did before calling this function)

        // CHECK IF IT'S A SKILL ADDED BY THE USER (in a file)
        output.append(this.userSkillRecognition.determineSkill(query));

        // THEN CHECK IF IT'S AN API SKILL WANTED
        if (output.isEmpty()) {
            String determinedSkill = this.apiSkillRecognition.determineSkill(query.toLowerCase(Locale.ROOT));
            for (SkillWrapper skill : this.skills.keySet()) {
                if (skill.getClass().getSimpleName().equals(determinedSkill)) {
                    String[] slots = this.skills.get(skill).findSlot(query);
                    skill.start(slots);
                    output.append(skill.getResponse());
                    break;
                }
            }
        }

        if (output.isEmpty()) {
            String determinedSkill = languageModel.determineSkill(query);
            for (SkillWrapper skill : this.skills.keySet()) {
                if (skill.getClass().getSimpleName().equals(determinedSkill)) {
                    String[] slots = languageModel.findSlot(query);
                    output.append(languageModel.botResponse(slots));
                    if (slots != null) {
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

    }
}
