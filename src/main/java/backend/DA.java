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
    }

    public void startQuery(String query) {
        // TODO: PROCESS INPUT AND BREAK IT DOWN USING CFG
        for (SkillWrapper skill : allMySkills) {
<<<<<<< HEAD
        // TODO: PROCESS INPUT AND BREAK IT DOWN USING CFG AND FIND OUT WHICH SKILL WE WANT TO USE
            // HARDCODE (needs changing)
            if(skill.getClass().getSimpleName().equals("Spotify")){
                // Assumption at this point
                didIUnderstand = 0.88;
                bestMatch = skill;
                // TODO: DEALING WITH THE PLACEHOLDERS USING CONTEXT-FREE GRAMMAR (CFG)
                matchedTemplate = "play <song>";
                ///////////////////////////
            }
            // END OF HARDCODE
        }
=======
>>>>>>> parent of 13a986a (Abstraction of backend)

        }
        // TODO:
    }
}
