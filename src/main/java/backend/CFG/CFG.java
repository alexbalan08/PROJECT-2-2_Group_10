package backend.CFG;

import java.util.Objects;

public class CFG {
    private final String filePath;
    private Sentences sentences;
    private Actions actions;

    public CFG(String path) {
        this.filePath = path;
        this.readFile();
    }

    public void readFile() {
        CFGReader reader = new CFGReader(this.filePath);
        this.sentences = new Sentences(reader.getRules());
        this.actions = new Actions(reader.getActions());
    }

    public String getAnswer(String input) {
        String type = this.sentences.findSentence(input);
        String answer = "I don't know ...";
        if(!Objects.equals(type, "")) {
            answer = this.actions.findAnswer(type, input);
        }
        return answer;
    }

    public String getExamplesOfQuestions() {
        return "Here are the skills I know, with a sample question for each : \n\n" + this.sentences.getExamplesOfQuestions();
    }
}
