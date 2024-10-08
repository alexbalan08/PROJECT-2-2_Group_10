package backend;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class SkillWrapper {

    public Queue<String> outputs = new LinkedList<>();
    // Needs to be overwritten to work
    public void start(List<String> slots) {
        outputs.add("Skill still in work.");
    }

    public String getResponse() {
        String finalOut = "";
        for (String out:outputs){
            finalOut+=outputs.remove();
        }
        return finalOut;
    }
}
