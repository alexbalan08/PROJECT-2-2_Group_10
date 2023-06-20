package backend.CFG;

import jep.*;

public class BERT {
    private final String scriptPath;
    private String actions;

    public BERT(String actions) {
        this.scriptPath = "src/main/java/backend/CFG/bert.py";
        this.actions = actions;
    }

    public String getAnswer(String input) {
        try (Interpreter interpreter = new SharedInterpreter()) {
            interpreter.set("text", this.actions);
            interpreter.set("question", input);
            interpreter.runScript(this.scriptPath);
            return interpreter.getValue("answer").toString();
        } catch (JepException e) {
            e.printStackTrace();
            return "Oh, I have a little problem here ...";
        }
    }

    public void getNewActions(String newAction) {
        this.actions = newAction;
    }
}
