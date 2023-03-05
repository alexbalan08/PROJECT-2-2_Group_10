package backend;

import backend.Skills.Canvas;
import backend.Skills.Google;
import backend.Skills.Spotify;
import backend.Skills.Weather;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DA {

    List<SkillWrapper> allMySkills;

    public DA() throws IOException {
        allMySkills = new ArrayList<>();
        addSkill(new Canvas());
        /*addSkill(new Google());
        addSkill(new Spotify());
        addSkill(new Weather());*/
    }

    private void addSkill(SkillWrapper skill) {
        allMySkills.add(skill);
        System.out.println(skill.getClass().getSimpleName() + " wrapper loaded successfully!");
    }

    public String startQuery(String query) throws IOException {
        if (query.contains("add skill:")) return addSkillToTextFile(query.replace("add skill:", "\n"));
        else return doSkill(query);
    }

    // Returns a String saying if adding the skill to the SkillsTemplate was successful
    public String addSkillToTextFile(String query) throws IOException {
        FileWriter fw = new FileWriter(new File("./src/main/java/backend/Skills/SkillsTemplate.txt"), true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(query);
        pw.flush();
        pw.close();
        bw.close();
        fw.close();
        return "Adding the skill was successful!";
    }

    public String doSkill(String query) throws IOException {
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

        didIUnderstand = 0.8; // DELETE LATER!!
        // Reality check
        if(didIUnderstand>=understandingThreshhold){
            bestMatch = new Canvas(); // DELETE LATER!!
            bestMatch.start(query);
            output=bestMatch.getResponse();
        }
        else {
            return ("Sorry, didn't understand you!");
        }
        return output;
    }
}
