package backend.CFG;

import java.util.Objects;

public class CFG {
    private final Sentences sentences;
    private final Actions actions;

    public CFG(String path) {
        CFGReader reader = new CFGReader(path);
        this.sentences = new Sentences(reader.getRules());
        this.actions = new Actions(reader.getActions());
        var t ="";
    }

    public String getAnswer(String input) {
        String type = this.sentences.findSentence(input);
        String answer = "I don't know ...";
        if(!Objects.equals(type, "")) {
            answer = this.actions.findAnswer(type, input);
        }
        return answer;
    }
}
