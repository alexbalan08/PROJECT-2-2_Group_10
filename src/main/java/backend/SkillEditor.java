package backend;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillEditor implements ActionQuery {
    HashMap<ArrayList<String>, Method> mapFunctions = new HashMap<>();
    String query;
    String key;
    public HashMap.Entry<ArrayList<String>, Method> entry;
    public ArrayList<String> addSkills = new ArrayList<>();
    public ArrayList<String> showSkills = new ArrayList<>();

    public SkillEditor() throws NoSuchMethodException {
        addSkillsToHashMap();
    }

    public SkillEditor(String query) throws NoSuchMethodException {
        addSkillsToHashMap();
        editSkill(query);
    }

    public void addSkillsToHashMap() throws NoSuchMethodException {
        addSkills.add("add skill:");
        addSkills.add("add the skill:");
        addSkills.add("add:");

        showSkills.add("show skills");
        showSkills.add("show all skills");
        showSkills.add("show all the skills");

        mapFunctions.put(addSkills, SkillEditor.class.getMethod("addSkill"));
        mapFunctions.put(showSkills, SkillEditor.class.getMethod("showSkills"));
    }

    public boolean editSkill(String query) {
        for (HashMap.Entry<ArrayList<String>, Method> entry : mapFunctions.entrySet()) {
            ArrayList<String> commands = entry.getKey();
            for (String command : commands) {
                if (query.contains(command)) {
                    this.entry = entry;
                    this.key = command;
                    return true;
                }
            }
        }
        return false;
    }

    public String startQuery(String query) throws InvocationTargetException, IllegalAccessException {
        this.query = query;
        if (entry == null) System.out.println("Check if the user wants to edit a skill");
        return (String) mapFunctions.get(entry.getKey()).invoke(this);
    }

    // Adds the skill to the text file and returns a String saying if adding the skill to the SkillsTemplate was successful
    public String addSkill() throws IOException {
        query = query.replace("add skill:", "");
        FileWriter fw = new FileWriter(new File("./src/main/java/backend/Skills/SkillsTemplate.txt"), true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(query);
        pw.flush();
        pw.close();
        bw.close();
        fw.close();
        System.out.println("ADDED SKILL");
        return "The skill has been added";
    }

   public String showSkills() throws IOException {
        return Files.readString(Path.of("./src/main/java/backend/Skills/SkillsTemplate.txt"));
   }

    /*public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SkillEditor editor = new SkillEditor();
        editor.
        System.out.println(editor.startQuery("add skill: htdrgsefrgdhf"));
    }*/

}
