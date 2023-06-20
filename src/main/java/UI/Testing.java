package UI;

import jep.*;

public class Testing {

    public static void runScript(String pythonScriptFullPath) {
        try (Interpreter interp = new SharedInterpreter()) {
            interp.set("text", "Olivier is 20 years old. Max has 2 more years than Olivier.");
            interp.set("question", "How old is Olivier ?");
            interp.runScript(pythonScriptFullPath);
            String answer = interp.getValue("answer").toString();
            System.out.println(answer);
        } catch (JepException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // runScript("src/main/java/UI/test.py");
        runScript("src/main/java/backend/CFG/bert.py");
    }
}

