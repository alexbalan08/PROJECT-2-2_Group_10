package backend;

import backend.Skills.Google;
import backend.Skills.Spotify;
import backend.Skills.Weather;

import java.util.ArrayList;
import java.util.List;

public class DA {

    List<SkillWrapper> allMySkills;

    public DA() {
        allMySkills = new ArrayList<>();
        addSkill(new Google());
        addSkill(new Spotify());
        addSkill(new Weather());
    }

    private void addSkill(SkillWrapper skill) {
        allMySkills.add(skill);
        System.out.println(skill.getClass().getSimpleName()+" wrapper loaded successfully!");
    }

    public void startQuery(String query) {
        double didIUnderstand = 0;
        double understandingThreshhold = 0.6;
        String matchedTemplate = null;
        SkillWrapper bestMatch = null;
        for (SkillWrapper skill : allMySkills) {
        // TODO: PROCESS INPUT AND BREAK IT DOWN USING CFG AND FIND OUT WHICH SKILL WE WANT TO USE
            // HARDCODE (needs changing)
            if(skill.getClass().getSimpleName().equals("Spotify")){
                // Assumption at this point
                didIUnderstand = 0.88;
                bestMatch = skill;
                matchedTemplate = "play <song>";
                ///////////////////////////
            }
            // END OF HARDCODE
        }

        // Reality check
        if(didIUnderstand>=understandingThreshhold){
            bestMatch.start(matchedTemplate);
        }
        else {
            System.out.println("Sorry, didn't understand you!");
        }
    }
}
