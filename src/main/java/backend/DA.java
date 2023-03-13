package backend;

import backend.Skills.*;
import backend.recognition.*;
import javafx.scene.control.TextArea;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DA implements ActionQuery {

    private final Map<SkillWrapper, SlotRecognition> skills;
    private final SkillRecognition skillRecognition;

    SkillEditor skillEditor;

    public DA() throws IOException, NoSuchMethodException {
        this.skills = new HashMap<>();
        this.addSkill(new Weather(), new WeatherSlotRecognition());
        this.addSkill(new Spotify(), new SpotifySlotRecognition());
        // this.addSkill(new Canvas(), new CanvasSlotRecognition());
        this.addSkill(new Wikipedia(), new WikipediaSlotRecognition());
        // this.addSkill(new Google(), ...);

        this.skillRecognition = new SkillRecognition();
    }

    private void addSkill(SkillWrapper skill, SlotRecognition slotRecognition) {
        this.skills.put(skill, slotRecognition);
        System.out.println(skill.getClass().getSimpleName() + " wrapper loaded successfully!");
    }

    public void instantiateSkillEditor(TextArea textArea, int maxNewLinesTextArea, int textHeight) throws IOException, NoSuchMethodException {
        skillEditor = new SkillEditor(textArea, maxNewLinesTextArea, textHeight);
    }

    public String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException {
        skillEditor.setQuery(query);
        if (skillEditor.isQueryToEditSkill()) {
            return skillEditor.startQuery(query);
        }
        else return doSkill(query);
    }

    public String doSkill(String query) throws IOException {
        StringBuilder output = new StringBuilder();
        String determinedSkill = this.skillRecognition.determineSkill(query.toLowerCase(Locale.ROOT));

        // CHECK IF IT'S A SKILL ADDED BY THE USER (in a file)
        // IF NOT, TRY TO FIND THE SKILL IN THE MAP

        for(SkillWrapper skill : this.skills.keySet()) {
            if(skill.getClass().getSimpleName().equals(determinedSkill)) {
                String[] slots = this.skills.get(skill).findSlot(query);
                skill.start(slots);
                output.append(skill.getResponse());
                break;
            }
        }

        if(!output.toString().equals("")) {
            return output.toString();
        } else {
            return "Sorry, I didn't understand you ...";
        }

    }
}
