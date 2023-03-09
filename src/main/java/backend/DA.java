package backend;

import backend.Skills.*;
import javafx.scene.control.TextArea;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DA implements ActionQuery {

    List<SkillWrapper> allMySkills;
    SkillEditor skillEditor;

    public DA() throws IOException, NoSuchMethodException {
        allMySkills = new ArrayList<>();
      //  addSkill(new Canvas());
        addSkill(new Google());
        addSkill(new Spotify());
        addSkill(new Weather());
        addSkill(new Wikipedia());
    }

    private void addSkill(SkillWrapper skill) {
        allMySkills.add(skill);
        System.out.println(skill.getClass().getSimpleName() + " wrapper loaded successfully!");
    }

    public void instantiateSkillEditor(TextArea textArea, int maxNewLinesTextArea, int textHeight) throws IOException, NoSuchMethodException {
        skillEditor = new SkillEditor(textArea, maxNewLinesTextArea, textHeight);
    }

    public String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException {
        skillEditor.setQuery(query);
        if (skillEditor.isQueryToEditSkill()) return skillEditor.startQuery(query);
        else return doSkill(query);
    }

    public String doSkill(String query) throws IOException {
        System.out.println(query);
        double didIUnderstand = 0;
        double understandingThreshhold = 0.6;
        String matchedTemplate = null;
        SkillWrapper bestMatch = null;
        String output = "";
        for (SkillWrapper skill : allMySkills) {
            // TODO: PROCESS INPUT AND BREAK IT DOWN USING CFG AND FIND OUT WHICH SKILL WE WANT TO USE
            // HARDCODE (needs changing)
            if(skill.getClass().getSimpleName().equals(query)){
                // Assumption at this point
                didIUnderstand = 0.89;
                bestMatch = skill;
                // TODO: DEALING WITH THE PLACEHOLDERS USING CONTEXT-FREE GRAMMAR (CFG)
//                matchedTemplate = "play <song>";
                ///////////////////////////
            }
            // END OF HARDCODE
        }

        // Reality check
        if(didIUnderstand>=understandingThreshhold){
            bestMatch.start(query);
            output=bestMatch.getResponse();
        }
        else {
            return ("Sorry, didn't understand you!");
        }
        return output;
    }
}
