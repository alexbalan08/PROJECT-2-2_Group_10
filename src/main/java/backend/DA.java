package backend;

import backend.Skills.*;
import backend.recognition.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DA implements ActionQuery {

    private final Map<SkillWrapper, SlotRecognition> skills;
    SkillEditor skillEditor = new SkillEditor();
    private final SkillRecognition skillRecognition;

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

        for(SkillWrapper skill : this.skills.keySet()) {
            if(skill.getClass().getSimpleName().equals(determinedSkill)) {
                String[] slots = this.skills.get(skill).findSlot(query);
                output.append(skill.getClass().getSimpleName());
                for(String slot : slots) {
                    output.append(" -- ").append(slot);
                }
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
