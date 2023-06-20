package UI;

import jep.*;

public class Testing {

    public static void runScript(String pythonScriptFullPath) {
        try (Interpreter interp = new SharedInterpreter()) {
            interp.runScript(pythonScriptFullPath);
        } catch (JepException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        runScript("src/main/java/UI/test.py");
    }
}

