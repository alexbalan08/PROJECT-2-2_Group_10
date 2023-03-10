package backend;

import backend.Skills.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DA implements ActionQuery {

    List<SkillWrapper> allMySkills;
    SkillEditor skillEditor = new SkillEditor();
    private final SkillRecognition skillRecognition;

    public DA() throws IOException, NoSuchMethodException {
        allMySkills = new ArrayList<>();
      //  addSkill(new Canvas());
        addSkill(new Google());
        addSkill(new Spotify());
        addSkill(new Weather());
        addSkill(new Wikipedia());
        this.skillRecognition = new SkillRecognition();
    }

    private void addSkill(SkillWrapper skill) {
        allMySkills.add(skill);
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
        SkillWrapper bestMatch = null;

        String output = "";
        String determinedSkill = this.skillRecognition.determineSkill(query.toLowerCase(Locale.ROOT));

        for (SkillWrapper skill : allMySkills) {
            if(skill.getClass().getSimpleName().equals(determinedSkill)) {
                bestMatch = skill;
                break;
            }
        }

        if(bestMatch != null){
            bestMatch.start(query);
            /*
                Need to find more information according the skill recognised
                    IF Weather --> try to find the city
                    IF Spotify --> try to find what the user is searching (play/stop music, what's the song, ect)
                    ...
             */
            output = bestMatch.getResponse();
        } else {
            return ("Sorry, I didn't understand you!");
        }
        return output;
    }
}
