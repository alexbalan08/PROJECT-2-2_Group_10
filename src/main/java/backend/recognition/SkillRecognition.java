package backend.recognition;

import java.io.IOException;

public interface SkillRecognition {

    String determineSkill(String input) throws IOException;

}
