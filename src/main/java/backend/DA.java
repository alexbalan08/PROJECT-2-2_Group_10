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

        }
        // TODO:
    }
}
